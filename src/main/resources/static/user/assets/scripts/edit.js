const $editForm = document.getElementById('edit-form');
const $retireForm = document.getElementById('retire-form');
const $retireBtn = $editForm['retire'];
const emailCodeVerifyButton = document.querySelector('#retire-form [name="emailCodeVerityButton"]');
const emailCodeInput = document.querySelector('#retire-form [name="emailCode"]');
const emailSaltInput = document.querySelector('#retire-form [name="emailSalt"]');
const emailInput = document.querySelector('#edit-form [name="email"]');
$retireBtn.addEventListener('click', (e) => {
    e.preventDefault();
    $retireForm.classList.toggle('visible');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', $editForm['email'].value);
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
            case'success':
                emailSaltInput.value = response.salt;
                dialog.showSimpleOk('인증', '성공적으로 인증번호를 이메일에 전달했습니다. 확인해 주세요.');
                break;
            case'failure_absent':
                dialog.showSimpleOk('인증', '이메일을 찾지 못하였습니다.');
                break;
            case'failure_unauthorized':
                dialog.showSimpleOk('인증', '인증코드를 전달하지 못했습니다.');
                break;
            default:
                break;
        }
    };
    xhr.open('POST', '/api/user/edit-email');
    xhr.send(formData);
});
emailCodeVerifyButton?.addEventListener('click', () => {
    const emailCode = emailCodeInput.value;
    const emailSalt = emailSaltInput.value;
    const email = emailInput.value;

    if (emailCode === '') {
        dialog.showSimpleOk('이메일 인증', '인증코드를 입력해주세요.');
        emailCodeInput.focus();
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', email);
    formData.append('code', emailCode);
    formData.append('salt', emailSalt);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청 중 오류');
            return;
        }

        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'failure':
                dialog.showSimpleOk('이메일 인증', '인증번호가 틀렸습니다. 다시 시도해주세요.');
                break;
            case 'success':
                dialog.showSimpleOk('이메일 인증', '이메일 인증에 성공하셨습니다.');
                break;
            default:
                break;
        }
    };

    xhr.open('PATCH', '/api/user/edit-email');
    xhr.send(formData);
});


$retireForm.querySelector(':scope>.confirm').addEventListener('click', (e) => {
    e.preventDefault();
    if (!$retireForm.querySelector(':scope>.check>input[name="agree"]').checked) {
        dialog.showSimpleOk('탈퇴', '탈퇴하기 위해서 필수로 확인해야하는 사항입니다.');
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', emailInput.value);
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
                dialog.show({
                    title: '탈퇴', content: '탈퇴에 성공하셨습니다.',
                    buttons: [{
                        caption: '확인',
                        color:
                            '#0478F8',
                        onOkCallback:
                            () => {
                                location.href = `${origin}/`;
                            }
                    }]
                });
                return;
            case'failure':
                dialog.showSimpleOk('탈퇴', '비밀번호가 달라 탈퇴에 실패하셨습니다.');
                break;
            case'failure_absent':
                dialog.showSimpleOk('탈퇴', '이미 탈퇴된 회원입니다.');
                break;
            default:
                return;
        }
    };
    xhr.open('PATCH', '/api/user/mypage/edit');
    xhr.send(formData);
})
$editForm.querySelector(':scope>.modify-btn').addEventListener('click',(e)=>{
    e.preventDefault();
    const nickname = $editForm.querySelector(':scope>.nickname-container>.label-object>input[name="nickname"]').value;

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('newNickname', nickname);
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
        switch (response.results){
            case'failure':
                dialog.showSimpleOk('수정','수정에 실패했습니다.');
                break;
            case'failure_duplicate':
                dialog.showSimpleOk('수정','닉네임이 중복됩니다.');
                break;
            case'success':
                dialog.showSimpleOk('수정','수정 완료했습니다.');
                break;
            default:
                break;
        }

    }
    xhr.open('PATCH', '/api/user/edit-update');
    xhr.send(formData);
})
