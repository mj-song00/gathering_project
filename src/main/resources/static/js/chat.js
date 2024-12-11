document.addEventListener("DOMContentLoaded", function () {
  const chatMessages = document.getElementById("chatMessages");
  const messageInput = document.getElementById("messageInput");
  const sendMessageButton = document.getElementById("sendMessageButton");
  const loadMoreButton = document.getElementById("loadMoreButton");
  const token = localStorage.getItem("token");
  const urlParams = new URLSearchParams(window.location.search);
  const roomId = urlParams.get("gatheringId");

  let currentPage = 0; // 현재 페이지 번호
  let currentUserId = null; // 현재 사용자 ID (senderId)

  // 로그인이 되어있지 않거나 gatheringId가 없으면 로그인 페이지로 이동
  if (!token || !roomId) {
    alert("로그인이 필요하거나 잘못된 모임입니다.");
    window.location.href = "/login.html";
    return;
  }

  // 본인 senderId를 서버에서 받아옵니다.
  fetch('/api/chat/me', {
    headers: {Authorization: `Bearer ${token}`}
  })
  .then(response => response.text())
  .then(userId => {
    currentUserId = userId;
  })
  .catch(error => console.error("Failed to fetch senderId:", error));

  // 시간을 포맷팅하는 함수
  function formatTime(dateStr, includeDate = false) {
    const date = new Date(dateStr);
    const options = includeDate
        ? {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
          hour12: true
        }
        : {hour: "2-digit", minute: "2-digit", hour12: true};
    return new Intl.DateTimeFormat("ko-KR", options).format(date);
  }

  // 메시지를 렌더링하는 함수
  function renderMessage(msg, prepend = false) {
    const messageElement = document.createElement("div");
    const time = msg.messageType === "CHAT" ? formatTime(msg.createdAt)
        : formatTime(msg.createdAt, true);
    const sender = msg.senderNickname || "시스템";

    // 텍스트의 URL을 클릭 가능한 링크로 변환하는 기능
    function linkify(text) {
      const urlPattern = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
      return text.replace(urlPattern,
          '<a href="$1" target="_blank" class="text-blue-500 hover:underline">$1</a>');
    }

    if (msg.messageType === "JOIN") {
      messageElement.classList.add("chat-bubble", "join");
      messageElement.innerHTML = `${msg.message.toLowerCase()}<br/>${time}`;
    } else if (msg.messageType === "LEAVE") {
      messageElement.classList.add("chat-bubble", "leave");
      messageElement.innerHTML = `${msg.message.toLowerCase()}<br/>${time}`;
    } else {
      messageElement.classList.add("chat-bubble",
          msg.senderId === currentUserId ? "sent" : "received");
      messageElement.innerHTML = `
      <div><strong>${sender}</strong></div>
      <p>${linkify(msg.message)}</p>
      <span class="text-xs text-gray-400">${time}</span>
    `;
    }

    if (prepend) {
      chatMessages.prepend(messageElement); // 상단에 이전 메시지 추가
    } else {
      chatMessages.appendChild(messageElement); // 하단에 새 메시지 추가
      messageElement.scrollIntoView({behavior: 'smooth', block: 'end'}); // 부드럽게 스크롤
    }
  }

  // 이전 메시지를 불러오는 함수
  function loadMessages() {
    fetch(`/api/chat/messages/${roomId}?page=${currentPage}&size=20`, {
      headers: {Authorization: `Bearer ${token}`},
    })
    .then(response => response.json())
    .then(data => {
      let messages = data.content;

      if (!Array.isArray(messages)) {
        console.error("Expected an array of messages, got:", messages);
        messages = []; // 빈 배열로 교체
      }

      messages.forEach(msg => renderMessage(msg, true));

      if (messages.length < 20) {
        loadMoreButton.style.display = "none"; // 더 이상 메시지가 없으면 버튼 숨김
      }

      if (messages.length > 0) {
        chatMessages.firstChild.scrollIntoView(
            {behavior: 'smooth', block: 'start'}); // 부드럽게 스크롤
      }

      currentPage++;
    })
    .catch(error => console.error("Failed to load messages:", error));
  }

  // 초기 메시지를 불러오는 함수
  function loadInitialMessages() {
    fetch(`/api/chat/messages/${roomId}?page=0&size=20&sort=createdAt,desc`, {
      headers: {Authorization: `Bearer ${token}`}
    })
    .then(response => response.json())
    .then(data => {
      data.content.forEach(renderMessage);
      chatMessages.scrollTop = chatMessages.scrollHeight; // 스크롤을 맨 아래로
    })
    .catch(error => console.error("Failed to load messages:", error));
  }

  // WebSocket 연결
  const socket = new SockJS('/ws');
  const stompClient = Stomp.over(socket);

  stompClient.connect({Authorization: `Bearer ${token}`}, function () {
    // 입장 메시지 전송
    fetch(`/api/chat/join/${roomId}`, {
      method: "POST",
      headers: {"Authorization": `Bearer ${token}`},
    }).catch(error => console.error("Failed to join room:", error));

    // 채팅방 구독
    stompClient.subscribe(`/topic/chat/${roomId}`, function (msg) {
      renderMessage(JSON.parse(msg.body));
    });
  });

  // 메시지 이벤트 리스너 보내기
  sendMessageButton.addEventListener("click", sendMessage);
  messageInput.addEventListener("keypress", function (e) {
    if (e.key === "Enter") {
      sendMessage();
    }
  });

  // 버튼 이벤트 리스너
  if (loadMoreButton) {
    loadMoreButton.addEventListener("click", loadMessages);
  }

  // 메시지 전송 함수
  function sendMessage() {
    const message = messageInput.value.trim();
    if (!message) {
      return;
    }

    fetch(`/api/chat/send/${roomId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({message})
    })
    .then(() => {
      messageInput.value = "";
    })
    .catch(error => console.error("Failed to send message:", error));
  }

  // 페이지 언로드 시 퇴장 메시지 전송
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
  loadInitialMessages();
});