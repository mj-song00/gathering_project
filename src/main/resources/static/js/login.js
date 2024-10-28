// 로그인 함수
async function login(event) {
  event.preventDefault();
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  const errorMessage = document.getElementById("errorMessage");

  errorMessage.style.display = "none";
  errorMessage.textContent = "";

  try {
    const response = await fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({email, password})
    });

    if (response.ok) {
      const token = response.headers.get("Authorization"); // Authorization 헤더에서 Bearer 토큰 추출
      if (token) {
        localStorage.setItem("token", token); // JWT 토큰을 로컬 스토리지에 저장
        window.location.href = "/home.html";  // 로그인 성공 후 홈 페이지로 이동
      }
    } else {
      const errorData = await response.json();
      errorMessage.style.display = "block";
      errorMessage.textContent = errorData.message || "로그인에 실패했습니다.";
    }
  } catch (error) {
    errorMessage.style.display = "block";
    errorMessage.textContent = "서버와 연결할 수 없습니다.";
  }
}

// 에러 메시지 표시 로직
function showErrorIfExists() {
  const errorMessage = document.getElementById("errorMessage");
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.has('error')) {
    errorMessage.style.display = "block";
    errorMessage.textContent = "로그인에 실패했습니다. 다시 시도해주세요.";
  }
}

// 카카오 소셜 로그인 함수
function kakaoLogin() {
  window.location.href = "/oauth2/authorization/kakao";
}

// 페이지 로드 후 응답 헤더에서 JWT 토큰 저장
document.addEventListener("DOMContentLoaded", async function () {
  const token = localStorage.getItem("token");
  if (token) {
  }
});
// 이벤트 리스너 설정
document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", login);
  }
  showErrorIfExists();  // 페이지 로드 시 에러 메시지 표시 여부 확인
});
