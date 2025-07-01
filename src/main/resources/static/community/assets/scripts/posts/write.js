const $main = document.getElementById('main');
const content = $main.querySelector(':scope > #writeForm > .text-container > .custom-placeholder');
const $subject = document.getElementById('subject');
const $posts = document.getElementById('posts');
const $title = $main.querySelector('input[name="title"]');
const $header = document.getElementById('header');
const $button = document.getElementById('uploadButton');
const $writeForm = document.getElementById('writeForm');
const titleRegex = new RegExp('^(.{1,60})$');
const contentRegex = new RegExp('^(.{1,100000})$')

const optionsMap = {
    "subject": [],
    "honey": ['집요한생일', '살까말까', '상품후기', '꿀잼수다'],
    "decoration": ['홈스타일링', '시공/모델링', '우리집일상']
}


$subject.addEventListener('change', () => {
    const selected = $subject.value;
    $posts.innerHTML = '';

    if (optionsMap[selected]) {
        optionsMap[selected].forEach(text => {
            const option = document.createElement('option');
            $subject.style.color = "black";
            $posts.style.color = "black";
            option.textContent = text;

            $posts.appendChild(option);
        });
    }

    $button.style.background = "rgb(53, 197, 240)";
    $button.style.border = "1px solid rgb(53, 197, 240)";
    $button.style.color = "white";
});


$button.addEventListener('click', () => {
    if ($subject.value === '주제를 선택해주세요(필수)') {
        dialog.showSimpleOk('게시글 작성', '주제를 선택해주세요', {
            onOkCallback: () => $subject.focus()
        });
        return;
    }

    if ($writeForm['title'].value === '') {
        dialog.showSimpleOk('게시글 작성', '제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (!titleRegex.test($writeForm['title'].value)) {
        dialog.showSimpleOk('게시글 작성', '올바른 제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (content.textContent === '') {
        dialog.showSimpleOk('게시글 작성', '내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }
    if (!titleRegex.test(content.textContent)) {
        dialog.showSimpleOk('게시글 작성', '올바른 내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("boardId", $subject.value);
    formData.append('content', content.textContent);
    formData.append('title', $writeForm['title'].value);
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
                dialog.showSimpleOk('게시글 등록', '게시글이 등록되었습니다.');
                break;
            default:
                dialog.showSimpleOk('게시글 등록', '알 수 없는 이유로 게시글을 등록하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('POST','/api/posts/new/');
    xhr.send(formData);
})

