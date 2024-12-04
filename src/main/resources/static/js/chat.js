document.addEventListener("DOMContentLoaded", function () {
  const chatMessages = document.getElementById("chatMessages");
  const chatContainer = document.getElementById("chatContainer");
  const messageInput = document.getElementById("messageInput");
  const sendMessageButton = document.getElementById("sendMessageButton");
  const loadMoreButton = document.getElementById("loadMoreButton");
  const token = localStorage.getItem("token");

  let currentPage = 0; // 페이지 번호 관리

  if (!token) {
    window.location.href = "/login.html";
    return;
  }

  const urlParams = new URLSearchParams(window.location.search);
  const roomId = urlParams.get("gatheringId");

  if (!roomId) {
    alert("잘못된 모임입니다.");
    window.location.href = "/home.html";
    return;
  }

  function renderMessages(messages, prepend = false) {
    if (!Array.isArray(messages)) {
      console.error("Expected an array of messages, got:", messages);
      messages = []; // 빈 배열로 교체
    }

    // 메시지 배열을 뒤집기
    if (prepend) {
      messages.reverse();
    }

    messages.forEach(msg => {
      const messageElement = document.createElement("div");
      if (msg.messageType === "JOIN") {
        messageElement.classList.add("text-green-500", "italic");
      } else if (msg.messageType === "LEAVE") {
        messageElement.classList.add("text-red-500", "italic");
      } else {
        messageElement.classList.add("bg-gray-100", "p-3", "rounded");
      }

      const formattedDate = new Date(msg.createdAt).toLocaleString('ko-KR', {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        hour12: true,
      });

      messageElement.innerHTML = `
      <div class="flex justify-between">
        <strong>${msg.senderNickname || "시스템"}</strong>
        <span class="text-sm text-gray-500">${formattedDate}</span>
      </div>
      <p>${msg.message}</p>
    `;

      if (prepend) {
        chatMessages.prepend(messageElement); // 오래된 메시지 앞에 추가
      } else {
        chatMessages.appendChild(messageElement); // 새 메시지 추가
      }
    });

    if (!prepend) {
      chatContainer.scrollTop = chatContainer.scrollHeight; // 새 메시지가 추가되면 스크롤 아래로
    }
  }

  function loadMessages() {
    const previousScrollHeight = chatContainer.scrollHeight;

    fetch(`/api/chat/messages/${roomId}?page=${currentPage}&size=20`, {
      headers: {Authorization: `Bearer ${token}`},
    })
    .then(response => response.json())
    .then(data => {
      const messages = data.content.reverse(); // 메시지 순서 바꾸기
      renderMessages(messages, true); // 오래된 메시지 앞에 추가

      if (messages.length < 20) {
        loadMoreButton.style.display = "none"; // 더 이상 메시지가 없으면 버튼 숨김
      }

      chatContainer.scrollTop = chatContainer.scrollHeight
          - previousScrollHeight; // 스크롤 위치 조정
      currentPage++;
    })
    .catch(error => console.error("Failed to load messages:", error));
  }

  loadMoreButton.addEventListener("click", loadMessages);

  // WebSocket 연결
  const socket = new SockJS('/ws');
  const stompClient = Stomp.over(socket);

  stompClient.connect({Authorization: `Bearer ${token}`}, function (frame) {
    console.log('Connected: ' + frame);

    fetch(`/api/chat/join/${roomId}`, {
      method: "POST",
      headers: {"Authorization": `Bearer ${token}`},
    }).catch(error => console.error("Failed to join room:", error));

    stompClient.subscribe(`/topic/chat/${roomId}`, function (messageOutput) {
      const message = JSON.parse(messageOutput.body);
      renderMessages([message]);
    });
  });

  sendMessageButton.addEventListener("click", sendMessage);
  messageInput.addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
      sendMessage();
    }
  });

  function sendMessage() {
    const message = messageInput.value.trim();
    if (message) {
      fetch(`/api/chat/send/${roomId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({message}),
      })
      .then(() => {
        messageInput.value = "";
      })
      .catch(error => console.error("Failed to send message:", error));
    }
  }

  window.addEventListener("beforeunload", function () {
    fetch(`/api/chat/leave/${roomId}`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      keepalive: true,
    }).catch(error => console.error("Leave room failed:", error));
  });

  // 초기 메시지 로드
  loadMessages();
});