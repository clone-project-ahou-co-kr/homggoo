const $main = document.getElementById('main');
const $like = $main.querySelector('button[name="like"]');
const $createdAt = $main.querySelector(':scope > .layout > .layout-content > .info > span > .relative-time');
const params = new URLSearchParams(window.location.search);
const $isLiked = document.getElementById('isLiked');
const $signedUser = document.getElementById('signedUser');
const $commentList = $main.querySelector('.comment-list');
const $commentButton = $main.querySelector(':scope > .layout > .comment-input > .input-container > .label > .commentButton');

const loadArticle = () => {

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("id", params.get('id'));

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'success':
                drawArticle(response.id, response.title, response.content, response.view, response.createdAt, response.likeCount, response.nickname, response.profile);
                loadComment(response.id);
                break;
            case 'failure':
                dialog.showSimpleOk('게시글 오류', '해당 게시판은 존재하지 않습니다.');
                break;
        }
    }
    xhr.open('POST',`/api/posts/`);
    xhr.send(formData);
}


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

const drawArticle = (id, title, content, view, createdAt, likeCount, nickname, profile) => {
    const $layoutContent = document.getElementById('drawArticle');
    $layoutContent.innerHTML = '';
    let html = `
            <div class="layout-row">
                <h1 class="content">홈스타일링</h1>
                <div class="button-container">
                    <a class="modify" href="#">수정</a>
                    <a class="delete" href="#">삭제</a>
                </div>
            </div>
            <div class="title">${title}</div>
            <div class="name">
                <div class="layout">
                    ${profile ? 
                    `<img src="${profile}" alt="프로필 이미지" style="width: 3rem; height: 3rem;">` : 
                    `<img src="/assets/images/index/header/default-profile.png" alt="기본 이미지">`}
                    <span class="email">${nickname}</span>
                </div>
                <div class="--flex-stretch"></div>
                <!--<div class="follow">팔로우</div>-->
            </div>
            <div class="content">${content}</div>
            <div class="info">
                <div class="layout">
                    <span><span class="relative-time">${getRelativeTime(createdAt)}</span> ·&nbsp;</span>
                    <span>좋아요 <span>${likeCount}</span>&nbsp;·&nbsp;</span>
                    <span>조회 <span>${view}</span></span>
                </div>
                <div class="--flex-stretch"></div>
                <!--<button class="&#45;&#45;object-button">신고하기</button>-->
            </div>
    `;
    $layoutContent.innerHTML = html;
    $isLiked.textContent = likeCount;
}

$like.addEventListener('click', () => {

    if(!$signedUser.value) {
        location.href = '/user/login';
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', params.get('id'));

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

        if (response.result === true) {
            $like.classList.add('-liked');
            loadArticle();
        } else if (response.result === false) {
            $like.classList.remove('-liked');
            loadArticle();
        }

    }
    xhr.open('PATCH','/api/posts/like');
    xhr.send(formData);
});

const loadComment = () => {

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
        const comments = JSON.parse(xhr.responseText);
        console.log(comments);
        const targetComments = comments.filter((comment) => comment.commentId == null);
        appendComments(targetComments, comments, 0);
    }
    xhr.open('GET','/api/posts/comment?id=' + params.get('id'));
    xhr.send();
}

const appendComments = (targetComments, wholeComments, step) => {
    for (const comment of targetComments) {
        $commentList.insertAdjacentHTML('beforeend', `
            <div class="comment">
                <div class="profile">
                    <img src="/assets/images/index/header/default-profile.png" alt="">
                </div>
                <div class="content-wrapper">
                    <div class="name-time">
                        <span class="nickname">${comment['userNickname']}</span>
                        <span class="time">${getRelativeTime(comment['createdAt'])}</span>
                    </div>
                    <div class="text">
                        ${comment['content']}
                    </div>
                    <div class="actions">
                        <button type="button">답글 달기</button><span>&nbsp·&nbsp</span>
                        <button type="button">❤ 좋아요</button><span>&nbsp·&nbsp</span>
                        <button type="button">신고</button>
                    </div>
                </div>
            </div>
        `)
        const nextComments = wholeComments.filter((nextComment) => nextComment.commentId === comment.id);
        if (nextComments.length > 0) {
            appendComments(nextComments, wholeComments, step + 1);
        }
    }
}

$commentButton.addEventListener('click', () => {

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('articleId', params.get('id'))
    formData.append('content', $main['comment'].value);
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
        switch (response.result) {
            case 'failure_session_expired':
                dialog.showSimpleOk('댓글', '로그인 후 작성해 주세요.', {
                    onOkCallback: location.href = "/user/login"
                })
                break;
            case 'failure':
                dialog.showSimpleOk('댓글', '잠시 후 다시 이용해 주세요.');
                break;
            case 'success':
                location.reload();
                break;

        }

    }
    xhr.open('POST','/api/posts/comment');
    xhr.send(formData);
})

loadArticle();


