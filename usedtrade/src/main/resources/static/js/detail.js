// ==============================
// 게시글 상세 페이지 DOM 요소
// ==============================

const detailStatus = document.querySelector("#detailStatus");

const mainImageBox = document.querySelector("#mainImageBox");
const thumbnailList = document.querySelector("#thumbnailList");

const postCategory = document.querySelector("#postCategory");
const postTitle = document.querySelector("#postTitle");
const postPrice = document.querySelector("#postPrice");
const postLocation = document.querySelector("#postLocation");
const postStatus = document.querySelector("#postStatus");
const postCreatedAt = document.querySelector("#postCreatedAt");
const postViewCount = document.querySelector("#postViewCount");
const postContent = document.querySelector("#postContent");

// ==============================
// 작성자 전용 컨트롤 요소
// ==============================

const ownerControls = document.querySelector("#ownerControls");
const statusSelect = document.querySelector("#statusSelect");
const statusUpdateButton = document.querySelector("#statusUpdateButton");
const editPostButton = document.querySelector("#editPostButton");
const deletePostButton = document.querySelector("#deletePostButton");

// ==============================
// 수정 모달 요소
// ==============================

const editPostModal = document.querySelector("#editPostModal");
const closeEditModalButton = document.querySelector("#closeEditModalButton");
const cancelEditModalButton = document.querySelector("#cancelEditModalButton");
const editPostForm = document.querySelector("#editPostForm");

const editTitle = document.querySelector("#editTitle");
const editPrice = document.querySelector("#editPrice");
const editCategory = document.querySelector("#editCategory");
const editLocation = document.querySelector("#editLocation");
const editContent = document.querySelector("#editContent");

// 현재 상세조회 중인 게시글 데이터
let currentPost = null;

// ==============================
// 상태 메시지 처리
// ==============================

function showDetailStatus(message) {
  if (!detailStatus) return;

  detailStatus.textContent = message;
  detailStatus.classList.remove("hidden");
}

function hideDetailStatus() {
  if (!detailStatus) return;

  detailStatus.textContent = "";
  detailStatus.classList.add("hidden");
}

// ==============================
// 표시용 변환 함수
// ==============================

function formatPrice(price) {
  return Number(price || 0).toLocaleString() + "원";
}

function convertStatus(status) {
  if (status === "SELLING") return "판매중";
  if (status === "RESERVED") return "예약중";
  if (status === "SOLD") return "판매완료";
  return status || "판매중";
}

function convertCategory(category) {
  if (category === "DIGITAL") return "디지털기기";
  if (category === "FURNITURE") return "가구";
  if (category === "CLOTHES") return "의류";
  if (category === "BOOK") return "도서";
  if (category === "ETC") return "기타";
  return category || "기타";
}

// ==============================
// 상품 판매상태 관련 함수
// ==============================
function getStatusClass(status) {
  if (status === "SELLING") return "selling";
  if (status === "RESERVED") return "reserved";
  if (status === "SOLD") return "sold";
  return "selling";
}

function updatePostStatusBadge(status) {
  if (!postStatus) return;

  postStatus.textContent = convertStatus(status);

  postStatus.classList.remove("selling", "reserved", "sold");
  postStatus.classList.add("product-status", getStatusClass(status));
}

// ==============================
// URL에서 게시글 번호 추출
// 예: /posts/3 -> 3
// ==============================

function getPostIdFromPath() {
  const paths = window.location.pathname.split("/");
  return paths[paths.length - 1];
}

// ==============================
// 상세 데이터 조회
// API: GET /api/posts/{postId}
// ==============================

async function loadPostDetail() {
  const postId = getPostIdFromPath();

  if (!postId) {
    showDetailStatus("게시글 번호를 찾을 수 없습니다.");
    return;
  }

  try {
    const response = await fetch(`/api/posts/${postId}`);

    if (!response.ok) {
      showDetailStatus("게시글을 불러오지 못했습니다.");
      return;
    }

    const post = await response.json();

    currentPost = post;

    renderPostDetail(post);
  } catch (error) {
    console.error(error);
    showDetailStatus("서버 연결에 실패했습니다.");
  }
}

// ==============================
// 상세 화면 렌더링
// ==============================

function renderPostDetail(post) {
  postTitle.textContent = post.title || "";
  postPrice.textContent = formatPrice(post.price);
  postCategory.textContent = convertCategory(post.category);
  postLocation.textContent = post.location || "지역 정보 없음";
  updatePostStatusBadge(post.status);
  postContent.textContent = post.content || "";

  const createdAt = post.createdAtPosts || "";
  postCreatedAt.textContent = createdAt
    ? `등록일 ${createdAt.slice(0, 10)}`
    : "등록일 정보 없음";

  postViewCount.textContent = `조회수 ${post.viewCount || 0}`;

  renderImages(post);
  renderOwnerControls(post);
}

// ==============================
// 이미지 영역 렌더링
// ==============================

function renderImages(post) {
  const images = post.images || [];

  if (images.length === 0) {
    mainImageBox.innerHTML = "<span>이미지 없음</span>";
    thumbnailList.innerHTML = "";
    return;
  }

  mainImageBox.innerHTML = `
    <img src="${images[0].imageUrl}" alt="${post.title || "상품 이미지"}" />
  `;

  thumbnailList.innerHTML = images
    .map(
      (image) => `
        <button type="button" class="detail-thumbnail">
          <img src="${image.imageUrl}" alt="${image.originalName || post.title || "상품 이미지"}" />
        </button>
      `,
    )
    .join("");

  const thumbnailButtons = document.querySelectorAll(".detail-thumbnail");

  thumbnailButtons.forEach((button, index) => {
    button.addEventListener("click", () => {
      mainImageBox.innerHTML = `
        <img src="${images[index].imageUrl}" alt="${images[index].originalName || post.title || "상품 이미지"}" />
      `;
    });
  });
}

// ==============================
// 작성자 전용 UI 표시
// post.owner가 true일 때만 수정/삭제/상태 변경 표시
// ==============================

function renderOwnerControls(post) {
  if (!ownerControls) return;

  if (!post.owner) {
    ownerControls.classList.add("hidden");
    return;
  }

  ownerControls.classList.remove("hidden");

  if (statusSelect) {
    statusSelect.value = post.status || "SELLING";
  }
}

// ==============================
// 수정 모달 처리
// ==============================

function fillEditModal(post) {
  if (
    !editTitle ||
    !editPrice ||
    !editCategory ||
    !editLocation ||
    !editContent
  ) {
    return;
  }

  editTitle.value = post.title || "";
  editPrice.value = post.price || "";
  editCategory.value = post.category || "ETC";
  editLocation.value = post.location || "";
  editContent.value = post.content || "";
}

function openEditModal() {
  if (!currentPost || !currentPost.owner) {
    showDetailStatus("게시글 작성자만 수정할 수 있습니다.");
    return;
  }

  if (!editPostModal) return;

  fillEditModal(currentPost);
  editPostModal.classList.remove("hidden");
}

function closeEditModal() {
  if (!editPostModal) return;

  editPostModal.classList.add("hidden");

  if (editPostForm) {
    editPostForm.reset();
  }
}

// ==============================
// 판매 상태 변경
// API: PATCH /api/posts/{postId}/status
// ==============================

async function handleStatusUpdate() {
  if (!currentPost || !currentPost.owner) {
    showDetailStatus("게시글 작성자만 상태를 변경할 수 있습니다.");
    return;
  }

  if (!statusSelect) return;

  const postId = getPostIdFromPath();
  const status = statusSelect.value;

  try {
    const response = await fetch(`/api/posts/${postId}/status`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ status }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      showDetailStatus(errorText || "상태 변경에 실패했습니다.");
      return;
    }

    hideDetailStatus();

    currentPost.status = status;
    updatePostStatusBadge(currentPost.status);

    alert("판매 상태가 변경되었습니다.");
  } catch (error) {
    console.error(error);
    showDetailStatus("서버 연결에 실패했습니다.");
  }
}

// ==============================
// 게시글 수정
// API: PUT /api/posts/{postId}
// ==============================

async function handlePostUpdate(event) {
  event.preventDefault();

  if (!currentPost || !currentPost.owner) {
    showDetailStatus("게시글 작성자만 수정할 수 있습니다.");
    return;
  }

  const postId = getPostIdFromPath();

  const updateData = {
    title: editTitle.value.trim(),
    price: Number(editPrice.value),
    category: editCategory.value,
    location: editLocation.value.trim(),
    content: editContent.value.trim(),
  };

  if (!updateData.title || !updateData.price || !updateData.content) {
    showDetailStatus("상품명, 가격, 상품 설명을 입력하세요.");
    return;
  }

  if (updateData.price <= 0) {
    showDetailStatus("가격은 0보다 커야 합니다.");
    return;
  }

  try {
    const response = await fetch(`/api/posts/${postId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(updateData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      showDetailStatus(errorText || "게시글 수정에 실패했습니다.");
      return;
    }

    hideDetailStatus();
    closeEditModal();

    await loadPostDetail();

    alert("게시글이 수정되었습니다.");
  } catch (error) {
    console.error(error);
    showDetailStatus("서버 연결에 실패했습니다.");
  }
}

// ==============================
// 게시글 삭제
// API: DELETE /api/posts/{postId}
// ==============================

async function handlePostDelete() {
  if (!currentPost || !currentPost.owner) {
    showDetailStatus("게시글 작성자만 삭제할 수 있습니다.");
    return;
  }

  const confirmDelete = confirm("정말 이 게시글을 삭제하시겠습니까?");

  if (!confirmDelete) {
    return;
  }

  const postId = getPostIdFromPath();

  try {
    const response = await fetch(`/api/posts/${postId}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      const errorText = await response.text();
      showDetailStatus(errorText || "게시글 삭제에 실패했습니다.");
      return;
    }

    alert("게시글이 삭제되었습니다.");
    location.href = "/";
  } catch (error) {
    console.error(error);
    showDetailStatus("서버 연결에 실패했습니다.");
  }
}

// ==============================
// 이벤트 등록
// ==============================

if (statusUpdateButton) {
  statusUpdateButton.addEventListener("click", handleStatusUpdate);
}

if (editPostButton) {
  editPostButton.addEventListener("click", openEditModal);
}

if (deletePostButton) {
  deletePostButton.addEventListener("click", handlePostDelete);
}

if (closeEditModalButton) {
  closeEditModalButton.addEventListener("click", closeEditModal);
}

if (cancelEditModalButton) {
  cancelEditModalButton.addEventListener("click", closeEditModal);
}

if (editPostForm) {
  editPostForm.addEventListener("submit", handlePostUpdate);
}

// 모달 바깥 영역 클릭 시 닫기
if (editPostModal) {
  editPostModal.addEventListener("click", function (event) {
    if (event.target === editPostModal) {
      closeEditModal();
    }
  });
}

// ==============================
// 초기 실행
// ==============================

loadPostDetail();
