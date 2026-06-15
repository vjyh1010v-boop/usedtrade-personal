// ==============================
// 상품 등록 모달 관련 요소
// ==============================
const openProductWriteButton = document.getElementById(
  "openProductWriteButton",
); // 상품 등록 버튼
const productWriteModal = document.getElementById("productWriteModal"); // 상품 등록 모달

const closeProductWriteButton = document.querySelector(
  "#closeProductWriteButton",
);
const productWriteForm = document.querySelector("#productWriteForm");

// ==============================
// 상품 목록 화면에서 사용하는 요소
// ==============================
const marketStatus = document.querySelector("#marketStatus");
const productCount = document.querySelector("#productCount");
const productList = document.querySelector("#productList");

// ==============================
// 검색, 카테고리, 정렬 필터 요소
// ==============================
const searchForm = document.querySelector("#searchForm");
const keywordSearch = document.querySelector("#keywordSearch");
const categoryFilter = document.querySelector("#categoryFilter");
const sortFilter = document.querySelector("#sortFilter");

// ==============================
// 상품 이미지 업로드 및 미리보기 요소
// 로그인하지 않은 경우 상품 등록 모달이 없으므로 null일 수 있음
// ==============================
const productImagesInput = document.querySelector("#productImages");
const imagePreviewList = document.querySelector("#imagePreviewList");

// 장터 이름 선택 변수
const marketPageTitle = document.querySelector("#marketPageTitle");
const marketPageSummaryText = document.querySelector("#marketPageSummaryText");

// 서버에서 받아온 상품 목록을 저장하는 배열
let products = [];
let currentPage = 1; // 추가
let isFetching = false; // 추가

function getMarketPageType() {
  const path = window.location.pathname;

  if (path === "/my/posts") {
    return "MY_POSTS";
  }

  if (path === "/wishlist") {
    return "WISHLIST";
  }

  return "ALL_POSTS";
}

function getProductsApiUrl() {
  const pageType = getMarketPageType();

  if (pageType === "MY_POSTS") {
    return "/api/posts/my";
  }

  if (pageType === "WISHLIST") {
    return "/api/posts/favorites";
  }

  return "/api/posts";
}

function applyMarketPageTitle() {
  const pageType = getMarketPageType();

  if (!marketPageTitle || !marketPageSummaryText) return;

  if (pageType === "MY_POSTS") {
    marketPageTitle.textContent = "내 판매글";
    marketPageSummaryText.textContent = "내가 등록한 판매글";
    return;
  }

  if (pageType === "WISHLIST") {
    marketPageTitle.textContent = "찜 목록";
    marketPageSummaryText.textContent = "내가 찜한 상품";
    return;
  }

  marketPageTitle.textContent = "상품 목록";
  marketPageSummaryText.textContent = "총";
}

// ==============================
// 상태 메시지 표시 함수
// 예: 상품 목록 로딩 실패, 상품 등록 실패 등
// ==============================
function showStatus(message) {
  if (!marketStatus) return;

  marketStatus.textContent = message;
  marketStatus.classList.remove("hidden");
}

// 상태 메시지를 숨기는 함수
function hideStatus() {
  if (!marketStatus) return;

  marketStatus.textContent = "";
  marketStatus.classList.add("hidden");
}

// ==============================
// URL 쿼리스트링 값을 검색 필터에 반영하는 함수
// 예: /products?category=DIGITAL&sort=LATEST
//
// 현재는 검색 버튼을 눌러도 URL 이동을 하지 않지만,
// 나중에 URL 검색 조건을 다시 쓸 수 있으므로 유지함.
// ==============================
function applySearchParams() {
  if (!keywordSearch || !categoryFilter || !sortFilter) return;

  const params = new URLSearchParams(window.location.search);

  const keyword = params.get("keyword") || "";
  const category = params.get("category") || "ALL";
  const sort = params.get("sort") || "LATEST";

  keywordSearch.value = keyword;
  categoryFilter.value = category;
  sortFilter.value = sort;
}

// 가격 표시 형식 변환
// 예: 10000 -> 10,000원
function formatPrice(price) {
  return Number(price || 0).toLocaleString() + "원";
}

// 서버에서 받은 상품 상태값을 화면에 표시할 한글로 변환
function convertStatus(status) {
  if (status === "SELLING") return "판매중";
  if (status === "RESERVED") return "예약중";
  if (status === "SOLD") return "판매완료";
  return status || "판매중";
}

// ==============================
// 서버에서 상품 목록을 불러오는 함수
// 비회원도 상품 목록을 볼 수 있어야 하므로 로그인 여부와 관계없이 실행됨
// 역할:
// 1. 현재 페이지 번호, 검색어, 카테고리, 정렬 조건으로 서버에 요청
// 2. 서버에서 받은 데이터를 products 배열에 저장
// 3. 실제 화면 출력은 renderProducts()에 맡김
// ==============================
async function loadProducts(isInitialLoad = false) {
  if (!productList || isFetching) return;

  isFetching = true;
  hideStatus();

  // 처음 불러오거나 검색 조건이 바뀐 경우 기존 목록 초기화
  if (isInitialLoad) {
    currentPage = 1;
    products = [];
  }

  const keyword = encodeURIComponent(
    keywordSearch ? keywordSearch.value.trim() : "",
  );

  let category = categoryFilter ? categoryFilter.value : "";
  const sort = sortFilter ? sortFilter.value.toLowerCase() : "latest";

  // 서버에는 전체 카테고리 값을 빈 문자열로 보냄
  if (category === "ALL") {
    category = "";
  }

  let url = "";

  if (
    getMarketPageType() === "MY_POSTS" ||
    getMarketPageType() === "WISHLIST"
  ) {
    url = getProductsApiUrl();
  } else {
    url = `${getProductsApiUrl()}?page=${currentPage}&searchKeyword=${keyword}&category=${category}&sortCondition=${sort}`;
  }

  try {
    const response = await fetch(url);

    if (!response.ok) {
      const errorText = await response.text();
      console.error(errorText);
      showStatus("상품 목록을 불러오지 못했습니다.");
      return;
    }

    const data = await response.json();

    // 첫 페이지면 새로 저장, 더보기면 기존 배열 뒤에 추가
    if (isInitialLoad) {
      products = data;
    } else {
      products = [...products, ...data];
    }

    // 더보기 버튼 표시 여부
    const loadMoreBtn = document.getElementById("loadMoreBtn");

    if (loadMoreBtn) {
      loadMoreBtn.style.display = data.length < 12 ? "none" : "inline-block";
    }

    // 화면 렌더링은 renderProducts()에서만 처리
    renderProducts();
  } catch (error) {
    console.error(error);
    showStatus("서버 연결에 실패했습니다.");
  } finally {
    isFetching = false;
  }
}

// 더보기 버튼에서 호출
function loadMorePosts() {
  currentPage++;
  loadProducts(false);
}

let searchTimer = null;

function reloadProductsRealtime() {
  clearTimeout(searchTimer);

  searchTimer = setTimeout(() => {
    currentPage = 1;
    loadProducts(true);
  }, 250);
}

// ==============================
// 상품 목록을 화면에 렌더링하는 함수
//
// 이 함수가 실행되는 시점에 현재 입력/선택된 값을 읽음.
// 따라서 카테고리나 정렬 select에 별도 change 이벤트를 걸지 않아도,
// 검색 버튼을 누르는 순간 현재 값이 한 번에 적용됨.
// ===============================
function renderProducts() {
  if (!productList || !productCount) return;

  // 상품 개수 표시
  productCount.textContent = products.length;

  // 상품이 없을 때
  if (products.length === 0) {
    productList.innerHTML =
      '<p class="empty" style="text-align:center;">등록된 상품이 없습니다.</p>';
    return;
  }

  productList.innerHTML = products
    .map((product) => {
      const createdAt = product.createdAtPosts || product.createdAt || "";
      const imageUrl = product.mainImageUrl;

      const isFavorited =
        product.favorited === true ||
        product.favorited === 1 ||
        product.favorited === "1";

      const heartImage = isFavorited
        ? "/images/heart-fill.png"
        : "/images/heart.png";

      const imageHtml = imageUrl
        ? `<img src="${imageUrl}" alt="${product.title}" loading="lazy" />`
        : `
          <div class="no-image">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="3" width="18" height="18" rx="2"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <polyline points="21 15 16 10 5 21"/>
            </svg>
            <span>이미지 없음</span>
          </div>
        `;

      return `
        <article class="product-card" data-detail-url="/posts/${product.postId}">
          <div class="product-image">
            ${imageHtml}
          </div>

          <div class="product-body">
            <div class="product-title-row">
              <h3 class="product-title">${product.title}</h3>
              <span class="seller-nickname">${product.nickname || "알 수 없음"}</span>
            </div>

            <p class="product-description">${product.content || ""}</p>

            <div class="product-meta">
              <strong>${formatPrice(product.price)}</strong>
              <span>${product.location || ""}</span>
            </div>

            <div class="product-status ${getStatusClass(product.status)}">
              ${convertStatus(product.status)}
            </div>

            <small class="created-at">
              등록일: ${createdAt ? createdAt.slice(0, 10) : ""}
            </small>
          </div>

          <button
            type="button"
            class="favorite-button"
            data-post-id="${product.postId}"
            data-favorited="${isFavorited ? "true" : "false"}"
            aria-label="찜하기"
          >
            <img
              src="${heartImage}"
              alt="찜"
              class="favorite-icon"
            />
          </button>
        </article>
      `;
    })
    .join("");
}

// ==============================
// 상품 등록 시 선택한 이미지 파일을 미리보기로 보여주는 함수
// 로그인 사용자에게만 상품 등록 모달이 있으므로 비회원일 때는 실행되지 않음
// ==============================
function renderImagePreview() {
  if (!productImagesInput || !imagePreviewList) return;

  const files = Array.from(productImagesInput.files);

  if (files.length === 0) {
    imagePreviewList.innerHTML =
      '<p class="image-preview-empty">선택된 이미지가 없습니다.</p>';
    return;
  }

  // 선택한 파일마다 임시 URL을 만들어 이미지 미리보기 출력
  imagePreviewList.innerHTML = files
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

// 이미지 미리보기 영역 초기화
function resetImagePreview() {
  if (!imagePreviewList) return;

  imagePreviewList.innerHTML =
    '<p class="image-preview-empty">선택된 이미지가 없습니다.</p>';
}

// 상품 등록 모달 열기
function openProductWriteModal() {
  if (!productWriteModal) return;

  productWriteModal.classList.remove("hidden");
}

// 상품 등록 모달 닫기
function closeProductWriteModal() {
  if (!productWriteModal) return;

  productWriteModal.classList.add("hidden");

  // 입력 폼 초기화
  if (productWriteForm) {
    productWriteForm.reset();
  }

  // 이미지 미리보기 초기화
  resetImagePreview();
}

// ==============================
// 상품 등록 폼 제출 처리 함수
// FormData를 이용해 텍스트 데이터와 이미지 파일을 함께 서버로 전송
// ==============================
async function handleProductSubmit(event) {
  event.preventDefault();

  const productTitleInput = document.querySelector("#productTitle");
  const productPriceInput = document.querySelector("#productPrice");
  const productCategoryInput = document.querySelector("#productCategory");
  const tradeLocationInput = document.querySelector("#tradeLocation");
  const productContentInput = document.querySelector("#productContent");

  // 상품 등록에 필요한 입력 요소가 없으면 중단
  if (
    !productTitleInput ||
    !productPriceInput ||
    !productCategoryInput ||
    !tradeLocationInput ||
    !productContentInput ||
    !productImagesInput
  ) {
    showStatus("상품 등록 화면을 찾을 수 없습니다.");
    return;
  }

  const title = productTitleInput.value.trim();
  const price = productPriceInput.value.trim();
  const category = productCategoryInput.value;
  const location = tradeLocationInput.value.trim();
  const content = productContentInput.value.trim();

  // 필수 입력값 검증
  if (!title || !price || !content || !category) {
    showStatus("상품명, 가격, 상품 설명, 카테고리를 입력하세요.");
    return;
  }

  // multipart/form-data 형식으로 보낼 데이터 생성
  const formData = new FormData();
  formData.append("title", title);
  formData.append("price", price);
  formData.append("category", category);
  formData.append("location", location);
  formData.append("content", content);

  // 선택한 이미지 파일들을 FormData에 추가
  const files = productImagesInput.files;

  for (const file of files) {
    formData.append("images", file);
  }

  try {
    const response = await fetch("/api/posts", {
      method: "POST",
      body: formData,
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(errorText);
      showStatus("상품 등록에 실패했습니다.");
      return;
    }

    // 등록 성공 시 상태메시지 숨김
    hideStatus();

    // 모달 닫고 입력값 초기화
    closeProductWriteModal();

    // 상품 목록 다시 불러오기
    window.location.reload();
  } catch (error) {
    console.error(error);
    showStatus("서버 연결에 실패했습니다.");
  }
}

// ==============================
// 이벤트 등록 영역
// 로그인 여부에 따라 HTML에 없는 요소가 있을 수 있으므로
// addEventListener 전에 반드시 요소 존재 여부를 확인함
// ==============================

// 상품 등록 버튼 클릭 이벤트_SY
if (openProductWriteButton) {
  openProductWriteButton.addEventListener("click", () => {
    // HTML 버튼에 저장해둔 로그인 여부 가져오기
    const isAuthenticated =
      openProductWriteButton.dataset.authenticated === "true";

    // HTML 버튼에 저장해둔 로그인 페이지 주소 가져오기
    const loginUrl = openProductWriteButton.dataset.loginUrl;

    // 비회원이면 로그인 페이지로 이동
    if (!isAuthenticated) {
      location.href = loginUrl;
      return;
    }

    // 로그인한 사용자면 상품 등록 모달 열기
    productWriteModal.classList.remove("hidden");
  });
}

// 모달 닫기 버튼 클릭 시 모달 닫기
if (closeProductWriteButton) {
  closeProductWriteButton.addEventListener("click", closeProductWriteModal);
}

// ==============================
// 상품 카드 클릭 이벤트
// ==============================
document.addEventListener("click", function (event) {
  const favoriteButton = event.target.closest(".favorite-button");

  if (favoriteButton) {
    return;
  }

  const productCard = event.target.closest(".product-card");

  if (!productCard) {
    return;
  }

  const detailUrl = productCard.dataset.detailUrl;

  if (!detailUrl) {
    return;
  }

  location.href = detailUrl;
});

// ==============================
// 찜 버튼 클릭 이벤트
// ==============================
document.addEventListener("click", async function (event) {
  const favoriteButton = event.target.closest(".favorite-button");

  // 클릭한 요소가 찜 버튼이 아니면 아무 동작 안 함
  if (!favoriteButton) return;

  // 찜 버튼을 눌렀을 때 상품 상세 페이지로 이동하지 않도록 막음
  event.preventDefault();
  event.stopPropagation();

  const postId = favoriteButton.dataset.postId;

  if (!postId) {
    alert("상품 정보를 찾을 수 없습니다.");
    return;
  }

  try {
    const response = await fetch(`/api/posts/${postId}/favorite`, {
      method: "POST",
    });

    // 비로그인 사용자가 찜 버튼을 누른 경우
    if (response.status === 401) {
      alert("로그인 후 찜할 수 있습니다.");
      location.href = "/users/login";
      return;
    }

    // 서버 처리 실패
    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      alert(errorData?.message || "찜 처리 중 오류가 발생했습니다.");
      return;
    }

    const data = await response.json();

    const favoriteIcon = favoriteButton.querySelector(".favorite-icon");

    // 서버에서 받은 찜 상태에 따라 이미지 변경
    if (data.favorited) {
      favoriteIcon.src = "/images/heart-fill.png";
      favoriteButton.dataset.favorited = "true";
    } else {
      favoriteIcon.src = "/images/heart.png";
      favoriteButton.dataset.favorited = "false";
    }

    // 전역 products 배열도 같이 갱신
    // 검색이나 정렬 후 다시 renderProducts()가 실행되어도 찜 상태가 유지되도록 하기 위함
    products = products.map((product) => {
      if (String(product.postId) !== String(postId)) {
        return product;
      }

      return {
        ...product,
        favorited: data.favorited,
        favoriteCount: data.favoriteCount,
      };
    });
  } catch (error) {
    console.error(error);
    alert("서버와 통신 중 오류가 발생했습니다.");
  }
});

if (productImagesInput) {
  productImagesInput.addEventListener("change", renderImagePreview);
}

// 상품 등록 폼 제출 이벤트
if (productWriteForm) {
  productWriteForm.addEventListener("submit", handleProductSubmit);
}

function getStatusClass(status) {
  if (status === "SELLING") return "selling";
  if (status === "RESERVED") return "reserved";
  if (status === "SOLD") return "sold";
  return "selling";
}

// URL 검색 조건을 화면 필터에 반영

// 로그인 여부와 관계없이 상품 목록 불러오기
// loadProducts();

if (searchForm) {
  searchForm.addEventListener("submit", function (e) {
    e.preventDefault();
    currentPage = 1;
    loadProducts(true);
  });
}

if (keywordSearch) {
  keywordSearch.addEventListener("input", reloadProductsRealtime);
}

if (categoryFilter) {
  categoryFilter.addEventListener("change", function () {
    currentPage = 1;
    loadProducts(true);
  });
}

if (sortFilter) {
  sortFilter.addEventListener("change", function () {
    currentPage = 1;
    loadProducts(true);
  });
}
applyMarketPageTitle();
applySearchParams();
loadProducts();
