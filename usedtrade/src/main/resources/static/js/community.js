const communityWriteModal = document.querySelector("#communityWriteModal");
const openCommunityWriteButton = document.querySelector(
  "#openCommunityWriteButton",
);
const closeCommunityWriteButton = document.querySelector(
  "#closeCommunityWriteButton",
);
const communityWriteForm = document.querySelector("#communityWriteForm");
const communityPostList = document.querySelector("#communityPostList");
const postCount = document.querySelector("#postCount");
const categoryFilter = document.querySelector("#categoryFilter");
const sortFilter = document.querySelector("#sortFilter");
const communityImagesInput = document.querySelector("#communityImages");
const communityImagePreviewList = document.querySelector(
  "#communityImagePreviewList",
);
const loadMoreButton = document.querySelector("#loadMoreButton");

const PAGE_SIZE = 12;
let currentOffset = 0;
let totalCount = 0;

function convertCategory(category) {
  if (category === "FREE") return "자유게시판";
  if (category === "LOCAL") return "동네정보";
  if (category === "REVIEW") return "거래후기";
  if (category === "QNA") return "질문게시판";
  return category;
}

function renderCard(post) {
  const createdAt = post.createdAt || "";
  const imageUrl = post.mainImageUrl;

  return `
    <article class="product-card" style="cursor:pointer" onclick="location.href='/community/${post.communityPostId}'">
      <div class="product-image">
        ${
          imageUrl
            ? `<img src="${imageUrl}" alt="${post.title}" />`
            : `
        <div class="no-image">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="18" height="18" rx="2"/>
            <circle cx="8.5" cy="8.5" r="1.5"/>
            <polyline points="21 15 16 10 5 21"/>
          </svg>
          <span>이미지 없음</span>
        </div>`
        }
      </div>

      <div class="product-body">
        <div class="product-status category-${post.category.toLowerCase()}">${convertCategory(post.category)}</div>
        <div class="product-title-row">
          <h3 class="product-title">${post.title}</h3>
        </div>
        <p class="product-description">${post.content || ""}</p>

        <div class="product-meta">
          <span>${post.nickname || ""}</span>
          <small>${createdAt ? createdAt.slice(0, 10) : ""}</small>
        </div>
        <div class="post-stats">
          <span class="post-like-count">👍 ${post.likeCount || 0}</span>
          <span class="post-view-count">👁 ${post.viewCount || 0}</span>
        </div>
      </div>
    </article>
  `;
}

function updateLoadMoreButton() {
  if (!loadMoreButton) return;
  if (currentOffset < totalCount) {
    loadMoreButton.classList.remove("hidden");
  } else {
    loadMoreButton.classList.add("hidden");
  }
}

function syncUrlParams(category, sort) {
  const params = new URLSearchParams();
  if (sort && sort !== "LATEST") params.set("sort", sort);
  if (category && category !== "ALL") params.set("category", category);
  const qs = params.toString();
  history.replaceState(null, "", "/community" + (qs ? "?" + qs : ""));
}

async function loadCommunityPosts() {
  if (!communityPostList) return;
  currentOffset = 0;

  const category = categoryFilter ? categoryFilter.value : "ALL";
  const sort = sortFilter ? sortFilter.value : "LATEST";

  syncUrlParams(category, sort);

  try {
    const res = await fetch(
      `/api/community/posts?category=${category}&sort=${sort}&offset=0&limit=${PAGE_SIZE}`,
    );

    if (!res.ok) {
      communityPostList.innerHTML =
        '<p class="empty">게시글을 불러오지 못했습니다.</p>';
      return;
    }

    const data = await res.json();
    totalCount = data.totalCount;
    if (postCount) postCount.textContent = totalCount;
    currentOffset = data.posts.length;

    if (data.posts.length === 0) {
      communityPostList.innerHTML = '<p class="empty">게시글이 없습니다.</p>';
    } else {
      communityPostList.innerHTML = data.posts.map(renderCard).join("");
    }
    updateLoadMoreButton();
  } catch (error) {
    console.error(error);
    communityPostList.innerHTML =
      '<p class="empty">서버 연결에 실패했습니다.</p>';
  }
}

async function loadMoreCommunityPosts() {
  const category = categoryFilter ? categoryFilter.value : "ALL";
  const sort = sortFilter ? sortFilter.value : "LATEST";

  try {
    const res = await fetch(
      `/api/community/posts?category=${category}&sort=${sort}&offset=${currentOffset}&limit=${PAGE_SIZE}`,
    );
    if (!res.ok) return;

    const data = await res.json();
    currentOffset += data.posts.length;
    communityPostList.insertAdjacentHTML(
      "beforeend",
      data.posts.map(renderCard).join(""),
    );
    updateLoadMoreButton();
  } catch (error) {
    console.error(error);
  }
}

function renderImagePreview() {
  if (!communityImagesInput || !communityImagePreviewList) return;

  const files = Array.from(communityImagesInput.files);

  if (files.length === 0) {
    communityImagePreviewList.innerHTML =
      '<p class="image-preview-empty">선택된 이미지가 없습니다.</p>';
    return;
  }

  communityImagePreviewList.innerHTML = files
    .map((file) => {
      const previewUrl = URL.createObjectURL(file);
      return `
        <div class="image-preview-item">
          <img src="${previewUrl}" alt="${file.name}" />
          <span>${file.name}</span>
        </div>
      `;
    })
    .join("");
}

function openCommunityWriteModal() {
  if (!communityWriteModal) return;
  communityWriteModal.classList.remove("hidden");
}

function closeCommunityWriteModalFn() {
  if (!communityWriteModal) return;
  communityWriteModal.classList.add("hidden");
  if (communityWriteForm) communityWriteForm.reset();
  if (communityImagePreviewList) {
    communityImagePreviewList.innerHTML =
      '<p class="image-preview-empty">선택된 이미지가 없습니다.</p>';
  }
}

async function handleCommunitySubmit(event) {
  event.preventDefault();

  const title = document.querySelector("#communityTitle")?.value.trim();
  const content = document.querySelector("#communityContent")?.value.trim();
  const category = document.querySelector("#communityCategory")?.value;

  if (!title || !content) {
    alert("제목과 내용을 입력하세요.");
    return;
  }

  const formData = new FormData();
  formData.append("title", title);
  formData.append("content", content);
  formData.append("category", category);

  if (communityImagesInput) {
    for (const file of communityImagesInput.files) {
      formData.append("images", file);
    }
  }

  try {
    const response = await fetch("/api/community/posts", {
      method: "POST",
      body: formData,
    });

    if (!response.ok) {
      alert("게시글 등록에 실패했습니다.");
      return;
    }

    closeCommunityWriteModalFn();
    await loadCommunityPosts();
  } catch (error) {
    console.error(error);
    alert("서버 연결에 실패했습니다.");
  }
}

if (openCommunityWriteButton) {
  openCommunityWriteButton.addEventListener("click", () => {
    const isAuthenticated =
      openCommunityWriteButton.dataset.authenticated === "true";
    const loginUrl = openCommunityWriteButton.dataset.loginUrl;

    if (!isAuthenticated) {
      location.href = loginUrl;
      return;
    }

    openCommunityWriteModal();
  });
}

if (closeCommunityWriteButton) {
  closeCommunityWriteButton.addEventListener(
    "click",
    closeCommunityWriteModalFn,
  );
}

if (categoryFilter) {
  categoryFilter.addEventListener("change", loadCommunityPosts);
}

if (sortFilter) {
  sortFilter.addEventListener("change", loadCommunityPosts);
}

if (loadMoreButton) {
  loadMoreButton.addEventListener("click", loadMoreCommunityPosts);
}

if (communityImagesInput) {
  communityImagesInput.addEventListener("change", renderImagePreview);
}

if (communityWriteForm) {
  communityWriteForm.addEventListener("submit", handleCommunitySubmit);
}

// 페이지 로드 시 URL 파라미터로 드롭다운 복원
(function restoreFromUrl() {
  const params = new URLSearchParams(window.location.search);
  const sort = params.get("sort");
  const category = params.get("category");
  if (sort && sortFilter) sortFilter.value = sort;
  if (category && categoryFilter) categoryFilter.value = category;
})();

loadCommunityPosts();
