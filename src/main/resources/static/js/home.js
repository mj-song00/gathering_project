document.addEventListener("DOMContentLoaded", function () {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "/login.html";
    return;
  }

  // 모임 목록 로드
  fetch('/api/gathers', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  .then(response => response.json())
  .then(data => {
    const myGathersList = document.getElementById("myGathersList");
    myGathersList.innerHTML = "";
    data.data.forEach(gather => {
      const gatherElement = document.createElement("div");
      gatherElement.classList.add("bg-white", "p-3", "rounded", "shadow-sm",
          "flex", "justify-between", "items-center");
      gatherElement.innerHTML = `
        <span>${gather.title}</span>
        <div class="flex items-center space-x-2">
          <a href="/chat.html?gatheringId=${gather.id}" class="text-blue-500 hover:text-blue-700">채팅</a>
        </div>
      `;
      myGathersList.appendChild(gatherElement);
    });
  });

  // 로그아웃
  const logoutButton = document.getElementById("logoutButton");
  logoutButton.addEventListener("click", function () {
    localStorage.removeItem("token");
    window.location.href = "/login.html";
  });
});