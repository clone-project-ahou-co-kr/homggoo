const $modifyForm = document.getElementById('modify-form');
$modifyForm.onsubmit = (e) => {
    e.preventDefault();
    const url = new URL(location.href);
    const index = url.searchParams.get("index");

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', $modifyForm['index'].value);
    formData.append('title', $modifyForm['title'].value);
    formData.append('content', $modifyForm.querySelector(':scope>.text-container>.custom-placeholder').textContent);
    formData.append('password', $modifyForm['password'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청중오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case'failure':
                dialog.showSimpleOk('수정', '실패');
                break;
            case'success':
                dialog.showSimpleOk('수정', '성공');
                history.back();
                break;
            default:
                break;
        }
    };
    xhr.open('PATCH', `/api/user/modify`);
    xhr.send(formData);
}

$modifyForm.querySelector(':scope>.button-container>.delete').addEventListener('click', () => {
    alert('삭제')
    const url = new URL(location.href);
    const index = url.searchParams.get("index");

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', $modifyForm['index'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청 중 오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case'failure':
                dialog.showSimpleOk('삭제', '실패');
                break;
            case'success':
                dialog.showSimpleOk('삭제', '성공');
                location.href = "/user/admin";
                break;
            default:
                break;
        }
    };
    xhr.open('DELETE', `/api/user/notice?index=${index}`);
    xhr.send(formData);
})
