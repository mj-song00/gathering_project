// 홈 화면 초기화
document.addEventListener("DOMContentLoaded", function () {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "/login.html";
  }
});

// 로그아웃 로직
document.getElementById("logoutButton").addEventListener("click", function () {
  localStorage.removeItem("token");
  window.location.href = "/login.html";
});

// 채팅방 이동 로직
function goToChatRoom() {
  const gatheringId = document.getElementById("gatheringIdInput").value;
  if (gatheringId) {
    window.location.href = `/chat.html?gatheringId=${gatheringId}`;
  } else {
    alert("모임 ID를 입력해주세요.");
  }
}
