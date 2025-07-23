const $modifyForm = document.getElementById('modify-form');
$modifyForm.onsubmit = (e) => {
    e.preventDefault();
    const url = new URL(location.href);
    const index = url.searchParams.get("index");
    $modifyForm['password'].classList.remove('warning');
    if ($modifyForm['password'].value === '') {
        dialog.show({
            title:'수정',
            content:'수정하시려면 비밀번호를 입력해주세요.',
            buttons:[
                {
                    caption:'확인',
                    color:'blue',
                    onclick:($modal)=>{
                        $modal.hide();
                        $modifyForm['password'].focus();
                        document.body.querySelector(':scope>.--dialog').classList.remove('-visible');
                    }
                }
            ]
        });
        $modifyForm['password'].classList.add('warning');
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', $modifyForm['index'].value);
    formData.append('title', $modifyForm['title'].value);
    formData.append('content', $modifyForm['content'].value);
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

$modifyForm.querySelector(':scope>.button-container>.buttons>.delete').addEventListener('click', () => {
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
                dialog.show(
                    {
                        title:'삭제',
                        content: '삭제하시는데 성공하셨습니다.',
                        buttons:[
                            {
                                caption:'확인',
                                color:'blue',
                                onclick:()=>{history.back()}
                            }
                        ]
                    }
                )
                break;
            default:
                break;
        }
    };
    xhr.open('DELETE', `/api/user/notice?index=${index}`);
    xhr.send(formData);
})
