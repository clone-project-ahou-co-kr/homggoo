const $noticeForm = document.getElementById('notice-form');
const $titleContainer = $noticeForm.querySelector(':scope>.title-container');
const $textContainer = $noticeForm.querySelector(':scope>.text-container');
$noticeForm.onsubmit = (e) => {
    e.preventDefault();
    const $title = $titleContainer.querySelector(':scope>label>input[name="title"]').value;
    const temp = document.createElement('div');
    temp.innerHTML = editor.getData();
    const textOnly = temp.textContent || temp.innerText || '';

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('title', $title);
    formData.append('content', editor.getData());
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청중 오류')
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case'failure':
                dialog.showSimpleOk('공지사항', '글쓰기 실패');
                break;
            case'failure_session_expired':
                dialog.showSimpleOk('공지사항', '없다.');
                break;
            case'success':
                dialog.showSimpleOk('공지사항', '글쓰기 성공');
                location.href = `${origin}/user/admin`
                break;
            default:
                dialog.showSimpleOk('공지사항', '알수없는오류');
                break;
        }
    };
    xhr.open('POST', '/api/user/notice');
    xhr.send(formData);
}