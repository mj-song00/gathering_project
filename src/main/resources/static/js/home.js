document.addEventListener("DOMContentLoaded", function () {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "/login.html";
    return;
  }

  // 모임 목록 로드
  fetch('/api/gathers', {
    headers: {'Authorization': `Bearer ${token}`}
  })
  .then(response => response.json())
  .then(data => {
    const myGathersList = document.getElementById("myGathersList");
    myGathersList.innerHTML = ""; // 기존 내용 초기화
    data.data.forEach(gather => {
      const gatherElement = document.createElement("div");
      gatherElement.classList.add(
          "bg-white", "p-3", "rounded", "shadow-sm",
          "flex", "justify-between", "items-center"
      );

      // 모임 제목 클릭 시 상세 페이지로 이동
      gatherElement.innerHTML = `
        <a href="/gatherDetail.html?gatherId=${gather.id}" 
           class="text-blue-500 font-semibold hover:text-blue-700">
           ${gather.title}
        </a>
        <div class="flex items-center space-x-2">
          <a href="/chat.html?gatheringId=${gather.id}" 
             class="text-blue-500 hover:text-blue-700">채팅</a>
        </div>
      `;
      myGathersList.appendChild(gatherElement);
    });
  })
  .catch(error => {
    console.error("모임 목록을 불러오는 중 오류가 발생했습니다:", error);
  });

  // 로그아웃
  const logoutButton = document.getElementById("logoutButton");
  logoutButton.addEventListener("click", function () {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
  });
});
