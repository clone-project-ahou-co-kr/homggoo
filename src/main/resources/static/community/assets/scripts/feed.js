const $notification = document.getElementById('notification');
const $info = $notification.querySelector(':scope > .layout');

const feed = () => {

    const xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('오류', '요청을 처리하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.', {
                onClickCallback: () => location.reload()
            });
            return;
        }
        const response = JSON.parse(xhr.responseText);
        drawList(response);
    }
    xhr.open('POST','/api/notification/');
    xhr.send();
}

const drawList = (notifications) => {
    $info.innerHTML = '';

    if (!notifications || notifications.length === 0) {
        $info.innerHTML = `<div class="empty">최근 내 소식이 없습니다.</div>`;
        return;
    }

    let html = '';
    for (const notification of notifications.slice(-10).reverse()) {
        if (notification.type === 'comment') {
            html += `
                <div class="comment info">
                    <img src="/community/assets/images/comment.png" alt="" class="favicon">
                    <a class="content" href="/community/posts?id=${notification.articleId}">
                        <span>커뮤니티에 내 게시글에 댓글이 달렸어요!</span>
                        <span>${getRelativeTime(notification.createdAt)}</span>
                    </a>
                </div>
            `;
        } else if (notification.type === 'article') {
            html += `
                <div class="comment info">
                    <img src="/community/assets/images/comment.png" alt="" class="favicon">
                    <a class="content" href="/community/posts?id=${notification.articleId}">
                        <span>커뮤니티에 내 게시글이 등록되었어요!</span>
                        <span>${getRelativeTime(notification.createdAt)}</span>
                    </a>
                </div>
            `;
        }
    }

    $info.innerHTML = html;
};

const getRelativeTime = (createdAtString) => {
    const createdAt = new Date(createdAtString);
    const now = new Date();
    const diffMs = now - createdAt;

    const diffMin = Math.floor(diffMs / 1000 / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);

    if (diffMin < 1) return "방금 전";
    if (diffMin < 60) return `${diffMin}분 전`;
    if (diffHour < 24) return `${diffHour}시간 전`;
    return `${diffDay}일 전`;
}

const deleteInfo = () => {

}

feed();