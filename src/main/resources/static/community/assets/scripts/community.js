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

    document.querySelectorAll('.item').forEach($item => {

        const board = $item.dataset.board;
        console.log(board)
        const categoryId = $item.dataset.categoryId;
        console.log(categoryId)
        const $category = $item.querySelector('.category');
        console.log($category)
        const $time = $item.querySelector('.time');
        console.log($time);

        const display = categoryMap[board]?.[categoryId];

        $category.textContent = display;

        const createdAtStr = $time?.textContent;
        if (createdAtStr) {
            const createdAt = new Date(createdAtStr);
            const now = new Date();
            const diffMs = now - createdAt;
            const diffMin = Math.floor(diffMs / 1000 / 60);
            const diffHour = Math.floor(diffMin / 60);
            const diffDay = Math.floor(diffHour / 24);

            let timeStr;
            if (diffMin < 1) timeStr = '방금 전';
            else if (diffMin < 60) timeStr = `${diffMin}분 전`;
            else if (diffHour < 24) timeStr = `${diffHour}시간 전`;
            else timeStr = `${diffDay}일 전`;

            $time.textContent = timeStr;
        }
    });

})
