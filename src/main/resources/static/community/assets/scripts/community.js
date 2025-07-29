
document.addEventListener('DOMContentLoaded', () => {


    const $main = document.getElementById('main');
    const $content = $main.querySelector(':scope > .board > .post-card > .content');
    const categoryMap = {
        product: {
            "product_popular": "인기",
            "product_free": "꿀템수다",
            "product_question": "살까말까",
            "product_review": "상품후기"
        },
        interior: {
            "sub_free": "우리집일상",
            "sub_popular": "인기",
            "sub_question": "홈스타일링"
        }
    };

    const formatPostCard = ($item) => {
        const board = $item.dataset.board;
        const categoryId = $item.dataset.categoryId;
        const $category = $item.querySelector('.category');
        const $time = $item.querySelector('.time');
        const display = categoryMap[board]?.[categoryId];

        if ($category) $category.textContent = display ?? '';

        const createdAtStr = $time?.textContent;
        if (createdAtStr) {
            const createdAt = new Date(createdAtStr);
            const now = new Date();
            const diffMin = Math.floor((now - createdAt) / 1000 / 60);
            const diffHour = Math.floor(diffMin / 60);
            const diffDay = Math.floor(diffHour / 24);

            let timeStr = '';
            if (diffMin < 1) timeStr = '방금 전';
            else if (diffMin < 60) timeStr = `${diffMin}분 전`;
            else if (diffHour < 24) timeStr = `${diffHour}시간 전`;
            else timeStr = `${diffDay}일 전`;

            $time.textContent = timeStr;
        }
    }

    let isLoading = false;
    // 초기 페이지를 1로 설정하여 먼저 loadMoreArticles 호출 후 그 후에 2페이지부터 렌더링
    let currentPage = 1;
    // 전역 변수로 선언하여 무한 로딩 방지
    let totalCount = null;
    // 5개 게시글 단위로 렌더링
    let rowCount = 5;
    // boardId, categoryId 가져오기
    const boardElement = document.getElementById("board");
    const boardId = boardElement.dataset.boardId;
    const params = new URLSearchParams(window.location.search);
    let categoryId = params.get("categoryId");
    if (categoryId === null) {
        categoryId = '';
    }

    const loadMoreArticles = () => {
        const xhr = new XMLHttpRequest();

        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) return;

            if (xhr.status < 200 || xhr.status >= 300) {
                alert("오류 발생. 다시 시도해 주세요.");
                return;
            }

            const response = JSON.parse(xhr.responseText);
            const articles = response.articles;
            const board = document.getElementById("board");

            if (response.totalCount === 0) {
                board.innerHTML = `
                    <div class="empty">게시물이 존재하지 않습니다.</div>
                `
            }

            // 최초 totalCount 한 번만 설정
            if (totalCount === null) {
                totalCount = response.totalCount;
                rowCount = response.rowCount;
            }

            // 현재까지 불러온 개수가 전체 게시글 수 이상이면 중단
            const loadedCount = (currentPage - 1) * rowCount;
            if (loadedCount >= totalCount) {
                console.log("없다 가라");

                window.removeEventListener("scroll", scrollHandler); // 더 이상 스크롤 이벤트도 막음
                return;
            }

            articles.forEach(article => {
                const card = document.createElement("div");
                card.className = "post-card";
                card.innerHTML = `
                <div class="item" data-board="${article.boardId}" data-category-id="${article.categoryId}">
                    <div class="category">#${article.categoryId}</div>
                    <a class="title" href="/community/posts?id=${article.id}">${article.title}</a>
                    <p class="content">${article.content}</p>
                    <div class="info">
                        <span class="username">${article.nickname}</span> · 
                        <span class="time">${article.createdAt}</span> · 
                        <span class="views">조회 ${article.view} · </span>
                        <span class="likes">❤ ${article.likeCount} · </span>
                        <span class="comments">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" color="#828C94" class="ey1aqhe0 css-bsbdow">
                                <path fill="currentColor" fill-rule="evenodd" d="m11.942 10.521.26-.516a4.948 4.948 0 1 0-2.145 2.17l.523-.27 1.886.519zm-1.193 2.986a6.448 6.448 0 1 1 2.794-2.827l.767 2.789a.65.65 0 0 1-.8.798z" clip-rule="evenodd"></path>
                            </svg>
                            ${article.commentCount}
                        </span>
                    </div>
                </div>`;
                board.appendChild(card);
                const $newItem = card.querySelector('.item');
                formatPostCard($newItem);
            });

            currentPage++;
            isLoading = false;
        };

        const url = `/api/posts/?page=${currentPage}&boardId=${boardId}&categoryId=${categoryId}`;
        xhr.open("GET", url);
        xhr.send();
    };

    const scrollHandler = () => {
        if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 300 && !isLoading) {
            isLoading = true;
            loadMoreArticles();
        }
    };
    loadMoreArticles();
    window.addEventListener("scroll", scrollHandler);
})
