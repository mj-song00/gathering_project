document.addEventListener("DOMContentLoaded", async () => {
  await getMemberId();
  loadGatherDetail();
});

const token = localStorage.getItem("token");
const urlParams = new URLSearchParams(window.location.search);
const gatherId = urlParams.get("gatherId");

if (!token || !gatherId) {
  alert("잘못된 접근입니다. 로그인 후 다시 시도해주세요.");
  window.location.href = "/login.html";
}

let schedules = [];
let currentItemId = null;
let currentType = null; // 'board' 또는 'schedule'
let currentMemberId = null; // 현재 로그인한 사용자의 memberId

// memberId 조회 함수
async function getMemberId() {
  try {
    const response = await fetch(`/api/members`, {
      headers: {Authorization: `Bearer ${token}`},
    });

    if (!response.ok) {
      throw new Error("멤버 정보를 불러올 수 없습니다.");
    }

    const {data} = await response.json();

    const member = data.find(m => m.gatherId === Number(gatherId));
    if (!member) {
      throw new Error("현재 유저는 해당 모임에 속한 멤버가 아닙니다.");
    }

    currentMemberId = member.id;
  } catch (error) {
    console.error("멤버 ID 조회 실패:", error);
    alert("멤버 정보를 불러오는 중 오류가 발생했습니다. 로그인 상태를 확인해주세요.");
  }
}

// 좋아요 버튼 이벤트 리스너 추가
document.getElementById("likeButton").addEventListener("click", incrementLike);

// 좋아요 증가 함수 (프론트에서 직접 증가시키지 않고 재조회로 갱신)
async function incrementLike() {
  if (!currentMemberId) {
    alert("멤버 정보가 없습니다. 다시 시도해주세요.");
    return;
  }

  try {
    const response = await fetch(
        `/api/likes/gather/${gatherId}/member/${currentMemberId}`, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

    if (!response.ok) {
      throw new Error("좋아요 증가 실패");
    }

    await loadGatherDetail();
  } catch (error) {
    console.error("좋아요 중 오류:", error);
    alert("좋아요 증가 중 오류가 발생했습니다.");
  }
}

// 모임 상세 정보 불러오기
async function loadGatherDetail() {
  try {
    const response = await fetch(`/api/gathers/${gatherId}/detail`, {
      headers: {Authorization: `Bearer ${token}`},
    });

    if (!response.ok) {
      throw new Error("모임 정보를 불러올 수 없습니다.");
    }
    const {data} = await response.json();

    document.getElementById("gatherTitle").textContent = data.title || "모임 상세";
    document.getElementById("gatherName").textContent = data.title;
    document.getElementById("gatherDescription").textContent = data.description;
    document.getElementById("likeCount").textContent = data.likeCount || 0;

    document.getElementById(
        "chatRoomLink").href = `/chat.html?gatheringId=${gatherId}`;

    schedules = data.scheduleInfos || [];
    renderList("scheduleList", schedules, "title", "schedule");
    renderList("boardList", data.boardInfos || [], "title", "board");

    loadMembers();
  } catch (error) {
    console.error("모임 정보 불러오기 실패:", error);
  }
}

// 리스트 렌더링
function renderList(elementId, items, key, type) {
  const element = document.getElementById(elementId);
  element.innerHTML = items.length
      ? items.map((item) => {
        const safeId = item.id || null;
        if (!safeId) {
          return `<p class="text-red-500">ID가 누락된 항목입니다.</p>`;
        }

        const title = item[key];
        const content = (type === 'schedule') ? item.scheduleContents
            : item.boardContents;

        return `
          <div class="bg-white p-4 rounded shadow-md">
            <h4 class="font-semibold mb-2">${title}</h4>
            <p class="text-gray-700">${content}</p>
            <button class="mt-2 text-blue-500 hover:underline"
              onclick="${type === 'schedule'
            ? `openDetailScheduleModal('${title}', '${content}', '${safeId}')`
            : `openDetailNoticeModal('${title}', '${content}', '${safeId}')`}">
              상세보기
            </button>
          </div>
        `;
      }).join("")
      : "<p>등록된 내용이 없습니다.</p>";
}

// 공지사항 작성 모달
function openCreateNoticeModal() {
  currentItemId = null;
  currentType = 'board';
  document.getElementById("noticeModalTitle").textContent = "공지사항 작성";
  document.getElementById("noticeModalInputTitle").value = "";
  document.getElementById("noticeModalInputContent").value = "";
  document.getElementById("noticeModalInputTitle").disabled = false;
  document.getElementById("noticeModalInputContent").disabled = false;
  document.getElementById("noticeModalSave").classList.remove("hidden");
  document.getElementById("noticeModalEdit").classList.add("hidden");
  document.getElementById("noticeModalDelete").classList.add("hidden");
  document.getElementById("noticeModal").classList.remove("hidden");
}

// 스케줄 작성 모달
function openCreateScheduleModal() {
  currentItemId = null;
  currentType = 'schedule';
  document.getElementById("scheduleModalTitle").textContent = "스케줄 작성";
  document.getElementById("scheduleModalInputTitle").value = "";
  document.getElementById("scheduleModalInputContent").value = "";
  document.getElementById("scheduleModalInputTitle").disabled = false;
  document.getElementById("scheduleModalInputContent").disabled = false;
  document.getElementById("scheduleModalSave").classList.remove("hidden");
  document.getElementById("scheduleModalEdit").classList.add("hidden");
  document.getElementById("scheduleModalDelete").classList.add("hidden");
  document.getElementById("scheduleModalComments").classList.add("hidden");
  document.getElementById("scheduleModal").classList.remove("hidden");
}

// 공지사항 상세 모달
function openDetailNoticeModal(title, content, id) {
  currentItemId = id;
  currentType = 'board';
  document.getElementById("noticeModalTitle").textContent = "공지사항 상세";
  document.getElementById("noticeModalInputTitle").value = title;
  document.getElementById("noticeModalInputContent").value = content;
  document.getElementById("noticeModalInputTitle").disabled = true;
  document.getElementById("noticeModalInputContent").disabled = true;
  document.getElementById("noticeModalSave").classList.add("hidden");
  document.getElementById("noticeModalEdit").classList.remove("hidden");
  document.getElementById("noticeModalDelete").classList.remove("hidden");
  document.getElementById("noticeModal").classList.remove("hidden");
}

// 스케줄 상세 모달
async function openDetailScheduleModal(title, content, id) {
  currentItemId = id;
  currentType = 'schedule';
  document.getElementById("scheduleModalTitle").textContent = "스케줄 상세";
  document.getElementById("scheduleModalInputTitle").value = title;
  document.getElementById("scheduleModalInputContent").value = content;
  document.getElementById("scheduleModalInputTitle").disabled = true;
  document.getElementById("scheduleModalInputContent").disabled = true;
  document.getElementById("scheduleModalSave").classList.add("hidden");
  document.getElementById("scheduleModalEdit").classList.remove("hidden");
  document.getElementById("scheduleModalDelete").classList.remove("hidden");
  document.getElementById("scheduleModalComments").classList.remove("hidden");
  document.getElementById("scheduleModal").classList.remove("hidden");
  await loadComments(id);
}

// 수정 버튼 이벤트
document.getElementById("noticeModalEdit").addEventListener("click", () => {
  document.getElementById("noticeModalInputTitle").disabled = false;
  document.getElementById("noticeModalInputContent").disabled = false;
  document.getElementById("noticeModalSave").classList.remove("hidden");
  document.getElementById("noticeModalEdit").classList.add("hidden");
});

document.getElementById("scheduleModalEdit").addEventListener("click", () => {
  document.getElementById("scheduleModalInputTitle").disabled = false;
  document.getElementById("scheduleModalInputContent").disabled = false;
  document.getElementById("scheduleModalSave").classList.remove("hidden");
  document.getElementById("scheduleModalEdit").classList.add("hidden");
});

// 공지사항 저장 버튼
document.getElementById("noticeModalSave").addEventListener("click",
    async () => {
      const title = document.getElementById(
          "noticeModalInputTitle").value.trim();
      const content = document.getElementById(
          "noticeModalInputContent").value.trim();

      if (!title || !content) {
        alert("제목과 내용을 모두 입력해주세요.");
        return;
      }

      try {
        let endpoint;
        let method;
        let body = {boardTitle: title, boardContent: content};

        if (currentItemId) {
          endpoint = `boards/${currentItemId}`;
          method = "PUT";
        } else {
          endpoint = "boards";
          method = "POST";
        }

        const response = await fetch(`/api/gathers/${gatherId}/${endpoint}`, {
          method: method,
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify(body),
        });

        if (!response.ok) {
          throw new Error(currentItemId ? "수정 실패" : "저장 실패");
        }

        alert(currentItemId ? "수정되었습니다." : "저장되었습니다.");
        closeNoticeModal();
        loadGatherDetail();
      } catch (error) {
        console.error(error);
        alert("처리 중 오류가 발생했습니다.");
      }
    });

// 스케줄 저장 버튼
document.getElementById("scheduleModalSave").addEventListener("click",
    async () => {
      const title = document.getElementById(
          "scheduleModalInputTitle").value.trim();
      const content = document.getElementById(
          "scheduleModalInputContent").value.trim();

      if (!title || !content) {
        alert("제목과 내용을 모두 입력해주세요.");
        return;
      }

      try {
        let endpoint;
        let method;
        let body = {scheduleTitle: title, scheduleContent: content};

        if (currentItemId) {
          endpoint = `schedules/${currentItemId}`;
          method = "PUT";
        } else {
          endpoint = "schedules";
          method = "POST";
        }

        const response = await fetch(`/api/gathers/${gatherId}/${endpoint}`, {
          method: method,
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify(body),
        });

        if (!response.ok) {
          throw new Error(currentItemId ? "수정 실패" : "저장 실패");
        }

        alert(currentItemId ? "수정되었습니다." : "저장되었습니다.");
        closeScheduleModal();
        loadGatherDetail();
      } catch (error) {
        console.error(error);
        alert("처리 중 오류가 발생했습니다.");
      }
    });

// 공지사항 삭제
document.getElementById("noticeModalDelete").addEventListener("click",
    async () => {
      const confirmDelete = confirm("정말로 삭제하시겠습니까?");
      if (!confirmDelete) {
        return;
      }

      const endpoint = `boards/delete/${currentItemId}`;

      try {
        const response = await fetch(`/api/gathers/${gatherId}/${endpoint}`, {
          method: "PATCH",
          headers: {Authorization: `Bearer ${token}`},
        });

        if (!response.ok) {
          throw new Error("삭제 실패");
        }

        alert("삭제되었습니다.");
        closeNoticeModal();
        loadGatherDetail();
      } catch (error) {
        console.error(error);
        alert("삭제 중 오류가 발생했습니다.");
      }
    });

// 스케줄 삭제
document.getElementById("scheduleModalDelete").addEventListener("click",
    async () => {
      const confirmDelete = confirm("정말로 삭제하시겠습니까?");
      if (!confirmDelete) {
        return;
      }

      const endpoint = `schedules/${currentItemId}/delete`;

      try {
        const response = await fetch(`/api/gathers/${gatherId}/${endpoint}`, {
          method: "PATCH",
          headers: {Authorization: `Bearer ${token}`},
        });

        if (!response.ok) {
          throw new Error("삭제 실패");
        }

        alert("삭제되었습니다.");
        closeScheduleModal();
        loadGatherDetail();
      } catch (error) {
        console.error(error);
        alert("삭제 중 오류가 발생했습니다.");
      }
    });

// 모달 닫기
document.getElementById("noticeModalCancel").addEventListener("click",
    closeNoticeModal);
document.getElementById("scheduleModalCancel").addEventListener("click",
    closeScheduleModal);

function closeNoticeModal() {
  document.getElementById("noticeModal").classList.add("hidden");
}

function closeScheduleModal() {
  document.getElementById("scheduleModal").classList.add("hidden");
}

// 멤버 불러오기
async function loadMembers() {
  try {
    const response = await fetch(`/api/members/${gatherId}`, {
      headers: {Authorization: `Bearer ${token}`},
    });
    const {data} = await response.json();

    document.getElementById("memberList").innerHTML = data.members
    .map((m) => `
        <div class="flex items-center bg-gray-100 p-4 rounded">
          <img src="${m.profileImage}" onerror="this.onerror=null;this.src='/img/default-profile.png';" class="w-12 h-12 rounded-full">
          <div class="ml-4">
            <p class="font-semibold">${m.nickName}</p>
            <p class="text-sm text-gray-500">${m.permission}</p>
          </div>
        </div>`).join("");
  } catch (error) {
    console.error("멤버 조회 실패:", error);
  }
}

// 댓글 불러오기
async function loadComments(itemId) {
  const commentList = document.getElementById("scheduleModalCommentList");

  try {
    const response = await fetch(`/api/schedule/${itemId}/comments`, {
      headers: {Authorization: `Bearer ${token}`},
    });

    if (!response.ok) {
      throw new Error("댓글을 불러오는데 실패했습니다.");
    }

    const {data} = await response.json();

    if (!data || !Array.isArray(data)) {
      throw new Error("댓글 데이터가 올바르지 않습니다.");
    }

    commentList.innerHTML = data.length
        ? data.map((c) => `
          <li class="flex items-start space-x-4 mb-4">
            <div>
              <p class="font-semibold">${c.nickName}</p>
              <p class="text-gray-700">${c.comment}</p>
              <p class="text-gray-500 text-sm">${new Date(
            c.createdAt).toLocaleString()}</p>
              <button class="text-blue-500 hover:underline" onclick="editComment('${c.commentId}', '${c.comment}')">수정</button>
              <button class="text-red-500 hover:underline" onclick="deleteComment('${c.commentId}')">삭제</button>
            </div>
          </li>`).join("")
        : "<li>댓글이 없습니다.</li>";
  } catch (error) {
    console.error(error);
    commentList.innerHTML = "<li>댓글을 불러오는 중 오류가 발생했습니다.</li>";
  }
}

async function editComment(commentId, currentComment) {
  const newComment = prompt("수정할 댓글 내용을 입력하세요:", currentComment);

  if (!newComment || newComment === currentComment) {
    return; // 변경사항 없으면 중단
  }

  try {
    const response = await fetch(
        `/api/schedule/${currentItemId}/gather/${gatherId}/comments/${commentId}`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify({comment: newComment}),
        }
    );

    if (!response.ok) {
      throw new Error("댓글 수정에 실패했습니다.");
    }

    alert("댓글이 수정되었습니다.");
    loadComments(currentItemId);
  } catch (error) {
    console.error("댓글 수정 오류:", error);
    alert("댓글 수정 중 오류가 발생했습니다.");
  }
}

async function deleteComment(commentId) {
  if (!confirm("정말로 이 댓글을 삭제하시겠습니까?")) {
    return;
  }

  try {
    const response = await fetch(
        `/api/schedule/${currentItemId}/gather/${gatherId}/comments/${commentId}/delete`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
    );

    if (!response.ok) {
      throw new Error("댓글 삭제에 실패했습니다.");
    }

    alert("댓글이 삭제되었습니다.");
    loadComments(currentItemId);
  } catch (error) {
    console.error("댓글 삭제 오류:", error);
    alert("댓글 삭제 중 오류가 발생했습니다.");
  }
}

// 댓글 작성
document.getElementById("scheduleModalAddCommentButton").addEventListener(
    "click", async () => {
      const commentInput = document.getElementById("scheduleModalCommentInput");
      const comment = commentInput.value.trim();

      if (!comment) {
        return alert("댓글 내용을 입력해주세요.");
      }

      try {
        const response = await fetch(
            `/api/schedule/${currentItemId}/gather/${gatherId}/comments`, {
              method: "POST",
              headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json"
              },
              body: JSON.stringify({comment}),
            });

        if (!response.ok) {
          throw new Error("댓글 작성에 실패했습니다.");
        }

        commentInput.value = "";
        loadComments(currentItemId);
      } catch (error) {
        console.error(error);
        alert("댓글 작성 중 오류가 발생했습니다.");
      }
    });
