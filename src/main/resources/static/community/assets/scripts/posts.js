const $main = document.getElementById('main');
const $like = $main.querySelector('button[name="like"]');
const params = new URLSearchParams(window.location.search);
const $isLiked = document.getElementById('isLiked');
const $signedUser = document.getElementById('signedUser') ?? { value: '' };
const $commentList = $main.querySelector('.comment-list');
const $commentsCount = $main.querySelector(':scope > .layout > .comment-content > .title > .comment-count');
const $sideCommentBtn = $main.querySelector(':scope > .side-actions > .icon-button.comment');


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
        // alert(response.userEmail)

        switch (response.result) {
            case 'success':
                drawArticle(response.id, response.title, response.content, response.view, response.createdAt, response.likeCount, response.nickname, response.profile, response.userEmail, response.categoryId);
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

const drawArticle = (id, title, content, view, createdAt, likeCount, nickname, profile, userEmail, categoryId) => {
    const $layoutContent = document.getElementById('drawArticle');
    let $category = '';
    switch (categoryId) {
        case "sub_popular":
            $category = '인기';
            break;
        case "sub_question":
            $category = '홈스타일링';
            break;
        case "sub_free":
            $category = '우리집일상';
            break;
        case "product_question":
            $category = '살까말까';
            break;
        case "product_popular":
            $category = '인기';
            break;
        case "product_review":
            $category = '상품후기';
            break;
        case "product_free":
            $category = '꿀템수다';
            break;
    }
    $layoutContent.innerHTML = '';

    let html = `
        <div class="layout-row">
            <h1 class="content">${$category}</h1>
            ${
        $signedUser.value === userEmail
            ? `<div class="button-container">
                    <a class="modify" href="/community/modify/${id}">수정</a>
                    <a class="delete" onclick="deleteArticle(${id})">삭제</a>
                </div>`
            : ''
    }
            
        </div>
        <div class="title">${title}</div>
        <div class="name">
            <div class="layout">
                ${
            profile
                ? `<img src="${profile}" alt="프로필 이미지" style="width: 3rem; height: 3rem;">`
                : `<img src="/assets/images/index/header/default-profile.png" alt="기본 이미지">`
        }
                <span class="email">${nickname}</span>
            </div>
            <div class="flex-stretch"></div>
        </div>
        <div class="content">${content}</div>
        <div class="info">
            <div class="layout">
                <span><span class="relative-time">${getRelativeTime(createdAt)}</span> ·&nbsp;</span>
                <span>좋아요 <span>${likeCount}</span>&nbsp;·&nbsp;</span>
                <span>조회 <span>${view}</span></span>
            </div>
            <div class="flex-stretch"></div>
            <!-- <button class="--object-button">신고하기</button> -->
        </div>
    `;
    $layoutContent.innerHTML = html;
    $isLiked.textContent = likeCount;
}

const deleteArticle = (id) => {

    if (!confirm('정말로 삭제하시겠습니까?')) {
        return;
    }

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
        switch (response.result) {
            case 'success':
                alert('삭제되었습니다.')
                location.href = "/";
                break;
            default:
                dialog.showSimpleOk('게시글 등록', '알 수 없는 이유로 게시글을 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('DELETE',`/api/posts/${id}`);
    xhr.send();
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
            location.reload();
        } else if (response.result === false) {
            $like.classList.remove('-liked');
            location.reload();
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
        $commentsCount.textContent = comments.length;
        appendComments(targetComments, comments, 0);
    }
    xhr.open('GET','/api/posts/comment?id=' + params.get('id'));
    xhr.send();
}

const appendComments = (targetComments, wholeComments, step) => {
    $main[name="comment"].value = '';
    for (const comment of targetComments) {
        const isReply = comment.commentId !== null;
        const replyTag = isReply ? `<span style="color: #0AA5FF;">@${comment.userNickname} &nbsp;</span>` : '';
        const replyBackgroundColor = step > 0 ? `background-color: rgb(247, 249, 250);` : ``;

        // noinspection CssInvalidPropertyValue
        $commentList.insertAdjacentHTML('beforeend', `
            <div class="comment" style="margin-left: ${step * 3}rem; ${replyBackgroundColor};" th:if="step != 0 ? ">
                <div class="profile">
                    <img src="/assets/images/index/header/default-profile.png" alt="">
                </div>
                <div class="content-wrapper">
                    <div class="name-time">
                        <span class="nickname">${comment['userNickname']}</span>
                        <span class="time">${getRelativeTime(comment['createdAt'])}</span>
                    </div>
                    <div class="text">${replyTag}${comment['content']}</div>
                    <div class="actions">
                        <button type="button" onclick="openReply(this)">답글 달기</button><!--<span>&nbsp·&nbsp</span>-->
                        <!--<button type="button">❤ 좋아요</button><span>&nbsp·&nbsp</span>
                        <button type="button">신고</button>-->
                    </div>
                </div>
            </div>
            <div class="reply-container"  style="margin-left: ${step * 3}rem; display: none;">
                <div class="profile">
                    <img src="/assets/images/index/header/default-profile.png" alt="">
                </div>
                <div class="input-container">
                    <label class="label" style="width: calc(100% - ${step * 3}rem);">
                        <span style="color: #0AA5FF;">@${comment['userNickname']}</span>
                        <input required type="text" minlength="1" name="reply" class="reply-input" placeholder="" size="44" style="width: calc(100% - 3rem);">                        
                        <button class="input-button" type="button" name="inputButton" data-type="reply" data-parent-id="${comment['id']}" data-nickname="${comment['userNickname']}">입력</button>
                    </label>
                </div>
            </div>
        `)
        const nextComments = wholeComments.filter((nextComment) => nextComment.commentId === comment.id);
        if (nextComments.length > 0) {
            appendComments(nextComments, wholeComments, 1);
        }
    }
}

const openReply = (button) => {
    const reply = button.parentElement.parentElement.parentElement.nextElementSibling;

    if (reply.style.display === 'flex') {
        reply.style.display = 'none';
    } else {
        reply.style.display = 'flex';
    }
}




document.addEventListener('click', (e) => {
    const $button = e.target.closest('.input-button');

    if (!$button) return;

    const type = $button.dataset.type;
    console.log(type)

    if (type === 'comment') {
        const content = $main.querySelector('input[name="comment"]').value;

        sendReply({parentId: null, content });
    }

    if (type === 'reply') {
        const parentId = $button.dataset.parentId;
        const content = $button.closest('.reply-container').querySelector('input.reply-input').value;
        document.querySelector('.reply-container').style.display = 'none';
        sendReply({ parentId, content });

    }
});

$sideCommentBtn.addEventListener('click', () => {
    $main.querySelector('input[name="comment"]').focus();
})

const sendReply = ({ parentId, content }) =>{
    if (!content || content.trim() === '') {
        dialog.showSimpleOk('대댓글', '내용을 입력해주세요.');
        return;
    }
    console.log(parentId);

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('articleId', params.get('id'));
    formData.append('commentId', parentId ?? '');
    formData.append('content', content);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('오류', '요청을 처리하는 도중 오류가 발생했습니다.', {
                onClickCallback: () => location.reload()
            });
            return;
        }
        $commentList.innerHTML = '';
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'failure_session_expired':
                dialog.showSimpleOk('댓글', '로그인 후 작성해 주세요.', {
                    onOkCallback: () => location.href = "/user/login"
                });
                break;
            case 'failure':
                dialog.showSimpleOk('댓글', '잠시 후 다시 시도해 주세요.');
                break;
            case 'success':
                loadComment();
                break;
        }
    };

    xhr.open('POST', '/api/posts/comment');
    xhr.send(formData);
}

loadArticle();


