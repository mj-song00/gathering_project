async function signup(event) {
  event.preventDefault();
  const email = document.getElementById("email").value;
  const nickName = document.getElementById("nickName").value;
  const password = document.getElementById("password").value;

  // 오류 메시지 초기화
  document.getElementById("emailError").style.display = "none";
  document.getElementById("emailError").textContent = "";
  document.getElementById("nickNameError").style.display = "none";
  document.getElementById("nickNameError").textContent = "";
  document.getElementById("passwordError").style.display = "none";
  document.getElementById("passwordError").textContent = "";

  const response = await fetch("/api/users/signup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({email, nickName, password, identityProvider: "NONE"})
  });

  const result = await response.json();

  if (response.ok) {
    // 성공 시 로그인 페이지로 이동
    window.location.href = "/login.html";
    
  } else if (response.status === 400 && result.data) {
    // 실패 시 필드별 오류 메시지 표시
    if (result.data.email) {
      const emailError = document.getElementById("emailError");
      emailError.textContent = result.data.email;
      emailError.style.display = "block";
    }
    if (result.data.nickName) {
      const nickNameError = document.getElementById("nickNameError");
      nickNameError.textContent = result.data.nickName;
      nickNameError.style.display = "block";
    }
    if (result.data.password) {
      const passwordError = document.getElementById("passwordError");
      passwordError.textContent = result.data.password;
      passwordError.style.display = "block";
    }
  } else {
    // 서버 오류 또는 기타 실패 시
    alert(result.message || "회원가입에 실패했습니다.");
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const signupForm = document.getElementById("signupForm");
  if (signupForm) {
    signupForm.addEventListener("submit", signup);
  }
});
