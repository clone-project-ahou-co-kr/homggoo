const $editForm = document.getElementById('edit-form');
const $retireForm = document.getElementById('retire-form');
const $retireBtn = $editForm['retire'];
$retireBtn.addEventListener('click', (e) => {
    e.preventDefault();
    $retireForm.classList.toggle('visible');
});
$retireForm.querySelector(':scope>.confirm').addEventListener('click', (e) => {
    e.preventDefault();
    if(!$retireForm.querySelector(':scope>.check>input[name="agree"]').checked){
        dialog.showSimpleOk('탈퇴', '탈퇴하기 위해서 필수로 확인해야하는 사항입니다.');
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("password", $retireForm.querySelector(':scope>label>.password').value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청 중 오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        console.log(response.result);
        switch (response.result) {
            case'success':
                dialog.showSimpleOk('탈퇴', '탈퇴에 성공하셨습니다.', {
                    caption: '확인',
                    color: '#0478F8',
                    onOkCallback: () => {
                        location.href = `${origin}/`;
                    }
                });
                return;
            case'failure':
                dialog.showSimpleOk('탈퇴', '비밀번호가 달라 탈퇴에 실패하셨습니다.');
                return;
            default:
                return;
        }
    };
    xhr.open('PATCH', '/api/user/mypage/edit');
    xhr.send(formData);
})