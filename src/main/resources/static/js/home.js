// 홈 페이지 로드 시 URL에서 토큰을 추출하여 로컬 스토리지에 저장
document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get("token");

  if (token) {
    localStorage.setItem("token", token);
    // URL에서 토큰 파라미터를 제거하여 클린 URL 유지
    window.history.replaceState({}, document.title, "/home.html");
  }

  // 로컬 스토리지에 토큰이 없는 경우 403 페이지로 리디렉트
  if (!localStorage.getItem("token")) {
    window.location.href = "error/403.html";
  } else {
    // JWT 토큰을 사용하여 보호된 리소스에 접근
    fetchProtectedResource();
  }
});

// 보호된 리소스에 접근하는 함수
async function fetchProtectedResource() {
  const token = localStorage.getItem("token");

  if (!token) {
    return;
  } // 토큰이 없는 경우 요청을 생략

  /*try {
    const response = await fetch("/api/protected-resource", { // 실제 API 엔드포인트로 변경
      method: "GET",
      headers: {
        "Authorization": token,  // "Bearer {token}" 형식이므로 그대로 사용
        "Content-Type": "application/json"
      }
    });

    if (response.ok) {
      const data = await response.json();
      console.log("Protected resource:", data);
    } else if (response.status === 403) {
      console.error(
          "Access forbidden: Invalid token or insufficient permissions.");
      window.location.href = "/login.html";
    } else {
      console.error("Request error:", response.status);
    }
  } catch (error) {
    console.error("Fetch error:", error);
  }*/
}

// 로그아웃 함수 정의
function logout() {
  localStorage.removeItem("token");  // JWT 토큰 제거
  window.location.href = "/login.html";  // 로그인 페이지로 리디렉트
}

// 로그아웃 버튼에 클릭 이벤트 리스너 추가
document.addEventListener("DOMContentLoaded", function () {
  const logoutButton = document.getElementById("logoutButton");
  if (logoutButton) {
    logoutButton.addEventListener("click", logout);
  }
});
