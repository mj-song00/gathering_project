let agreementIds = {}; // 약관 UUID 저장

// 약관 UUID 가져오기 및 렌더링
async function loadAgreements() {
  const termsContainer = document.getElementById("termsContainer");
  try {
    const response = await fetch("/api/agreements/latest/all");
    if (response.ok) {
      const result = await response.json();
      termsContainer.innerHTML = ""; // 초기화
      result.data.forEach((agreement) => {
        agreementIds[agreement.type] = agreement.id;
        const isRequired = agreement.type
            !== "MARKETING_INFO_RECEIVE_AGREEMENT";
        const checkbox = `
          <label class="block mb-2">
            <input type="checkbox" id="${agreement.type}" value="${agreement.id}" class="mr-2">
            <span class="text-sm text-gray-700">${agreement.type
        === "PRIVACY_POLICY" ? "개인정보 처리방침" :
            agreement.type === "TERMS_OF_SERVICE" ? "이용 약관"
                : "마케팅 정보 수신 동의"} ${isRequired ? "(필수)" : "(선택)"}
              <button type="button" class="text-blue-500 underline text-sm" onclick="viewAgreement('${agreement.id}')">
                자세히 보기
              </button>
            </span>
          </label>
        `;
        termsContainer.insertAdjacentHTML("beforeend", checkbox);
      });
    } else {
      termsContainer.innerHTML = "<p>약관 정보를 불러올 수 없습니다.</p>";
    }
  } catch (error) {
    console.error("약관 로드 실패:", error);
    termsContainer.innerHTML = "<p>약관 정보를 불러오는 중 오류가 발생했습니다.</p>";
  }
}

// 모달 열기
function openModal(title, content) {
  document.getElementById("modalTitle").textContent = title;
  document.getElementById("modalContent").innerHTML = content;
  const modal = document.getElementById("modal");
  modal.classList.remove("hidden");
  modal.classList.add("flex");
}

// 모달 닫기
function closeModal() {
  const modal = document.getElementById("modal");
  modal.classList.add("hidden");
  modal.classList.remove("flex");
}

// 약관 내용 보기
async function viewAgreement(agreementId) {
  try {
    const response = await fetch(`/api/agreements/${agreementId}`);
    if (response.ok) {
      const result = await response.json();
      openModal("약관 상세", result.data.content);
    } else {
      openModal("오류", "약관 내용을 가져올 수 없습니다.");
    }
  } catch (error) {
    console.error("약관 내용 가져오기 실패:", error);
    openModal("오류", "약관 내용을 가져오는 중 문제가 발생했습니다.");
  }
}

// 이메일 인증 코드 발송
async function sendVerificationCode() {
  const email = document.getElementById("email").value;
  if (!email) {
    document.getElementById("emailError").textContent = "이메일을 입력해주세요.";
    document.getElementById("emailError").classList.remove("hidden");
    return;
  }
  try {
    const response = await fetch("/api/verification/send", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({email}),
    });
    if (response.ok) {
      openModal("인증 코드 발송", "인증 코드가 이메일로 발송되었습니다.");
      const verificationContainer = document.getElementById(
          "verificationContainer");
      verificationContainer.innerHTML = `
        <input type="text" id="verificationCode" placeholder="인증 코드를 입력해주세요."
               class="w-full px-3 py-2 border rounded mt-2">
        <button type="button" id="verifyButton" onclick="confirmVerificationCode()"
                class="w-full py-2 px-4 bg-green-500 text-white font-semibold rounded hover:bg-green-600 mt-2">
          인증 확인
        </button>
      `;
    } else {
      const result = await response.json();
      openModal("오류", result.message || "인증 코드 발송에 실패했습니다.");
    }
  } catch (error) {
    openModal("오류", "서버와 통신 중 문제가 발생했습니다.");
  }
}

// 인증 코드 확인
async function confirmVerificationCode() {
  const email = document.getElementById("email").value;
  const code = document.getElementById("verificationCode").value;
  try {
    const response = await fetch("/api/verification/confirm", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({email, code}),
    });
    if (response.ok) {
      openModal("인증 성공", "이메일 인증이 완료되었습니다.");
      // 인증 필드와 버튼 비활성화
      document.getElementById("verificationCode").setAttribute("disabled",
          "true");
      const verifyButton = document.getElementById("verifyButton");
      verifyButton.setAttribute("disabled", "true");
      verifyButton.classList.add("bg-gray-500", "cursor-not-allowed");
      verifyButton.classList.remove("hover:bg-green-600");
    } else {
      const result = await response.json();
      openModal("인증 실패", result.message || "인증 실패");
    }
  } catch (error) {
    console.error("인증 확인 실패:", error);
    openModal("오류", "인증 확인 중 문제가 발생했습니다.");
  }
}

// 회원가입 폼 제출
document.getElementById("signupForm").addEventListener("submit",
    async function (e) {
      e.preventDefault();

      const email = document.getElementById("email").value;
      const nickName = document.getElementById("nickName").value;
      const password = document.getElementById("password").value;
      const agreedAgreementIds = Array.from(
          document.querySelectorAll("#termsContainer input:checked")
      ).map((cb) => cb.value);

      // 필수 약관 확인
      if (!document.getElementById("PRIVACY_POLICY").checked
          || !document.getElementById("TERMS_OF_SERVICE").checked) {
        document.getElementById("termsError").classList.remove("hidden");
        return;
      }
      document.getElementById("termsError").classList.add("hidden");

      // 에러 메시지 초기화
      document.getElementById("emailError").classList.add("hidden");
      document.getElementById("nickNameError").classList.add("hidden");
      document.getElementById("passwordError").classList.add("hidden");

      try {
        const response = await fetch("/api/users/signup", {
          method: "POST",
          headers: {"Content-Type": "application/json"},
          body: JSON.stringify({
            email,
            nickName,
            password,
            agreedAgreementIds,
            identityProvider: "NONE",
          }),
        });

        if (response.ok) {
          openModal("회원가입 성공", "회원가입이 완료되었습니다.");
          setTimeout(() => (window.location.href = "/login.html"), 2000);
        } else {
          const result = await response.json();

          // 필드별 에러 메시지 처리
          if (result.data) {
            if (result.data.email) {
              document.getElementById(
                  "emailError").textContent = result.data.email;
              document.getElementById("emailError").classList.remove("hidden");
            }
            if (result.data.nickName) {
              document.getElementById(
                  "nickNameError").textContent = result.data.nickName;
              document.getElementById("nickNameError").classList.remove(
                  "hidden");
            }
            if (result.data.password) {
              document.getElementById(
                  "passwordError").textContent = result.data.password;
              document.getElementById("passwordError").classList.remove(
                  "hidden");
            }
          } else {
            openModal("회원가입 실패", result.message || "회원가입 실패");
          }
        }
      } catch (error) {
        console.error("회원가입 실패:", error);
        openModal("오류", "회원가입 중 문제가 발생했습니다.");
      }
    });

// 페이지 로드 시 초기화
document.addEventListener("DOMContentLoaded", loadAgreements);
