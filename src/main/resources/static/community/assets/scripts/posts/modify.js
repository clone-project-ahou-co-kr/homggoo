const $uploadButton = document.getElementById('uploadButton');
const $modifyForm = document.getElementById('modifyForm');
const $title = document.getElementById('title');
const $content = document.getElementById('content');

$uploadButton.addEventListener('click', () => {

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    const url = new URL(location.href);
    const paths = url.pathname.split('/');
    const id = paths[3];

    formData.append('title', $title.value);
    formData.append('content', $content.textContent);

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
                dialog.showSimpleOk('게시글 등록', '게시글이 등록되었습니다.', {
                    onOkCallback : () => location.href = `/community/posts?id=${id}`
                });
                break;
            default:
                dialog.showSimpleOk('게시글 등록', '알 수 없는 이유로 게시글을 수정하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH',`/api/posts/${id}`);
    xhr.send(formData);
})