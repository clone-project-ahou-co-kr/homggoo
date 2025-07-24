const $loginForm = document.getElementById('login-form');

$loginForm.querySelector(':scope > .order-container > .unsigned-user')
    .addEventListener('click', () => {
        const $inputOrder = $loginForm.querySelector(':scope > .order-container > .input-order');

        if ($inputOrder.classList.contains('visible')) {
            // 숨기기 시작
            $inputOrder.classList.remove('visible'); // visible 제거 (opacity 관련)

        } else {
            // 보이기
            $inputOrder.classList.add('visible'); // fadeIn 작동
        }
    });

$loginForm.onsubmit = (e) => {
    e.preventDefault();
    if ($loginForm['loginEmail'].value === '') {
        dialog.showSimpleOk('로그인', '이메일을 입력해주세요');
        $loginForm['loginEmail'].focus();
        return;
    } else if (!$loginForm['loginEmail'].validity.valid) {
        dialog.showSimpleOk('경고', '이메일이 올바르지 않습니다.');
        $loginForm['loginEmail'].focus();
        return;
    }
    if ($loginForm['loginPassword'].value === '') {
        dialog.showSimpleOk('로그인', '비밀번호를 입력해주세요');
        $loginForm['loginPassword'].focus();
        return;
    } else if (!$loginForm['loginPassword'].validity.valid) {
        dialog.showSimpleOk('로그인', '비밀번호가 올바르지 않습니다.');
        $loginForm['loginPassword'].focus();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', $loginForm['loginEmail'].value);
    formData.append('password', $loginForm['loginPassword'].value);
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
            case 'failure':
                dialog.showSimpleOk('로그인', '로그인에 실패하였습니다. 비밀번호 또는 이메일을 확인해주세요.');
                break;
            case'success':
                location.href = '/';
                break;
            case'failure_session_expired':
                dialog.showSimpleOk('로그인', '탈퇴한 이메일 입니다.');
                break;
            case'failure_absent':
                dialog.showSimpleOk('로그인', '일치하는 정보를 찾지 못하였습니다.');
                break;
            default:
                break;
        }
    };
    xhr.open('POST', '/api/user/login');
    xhr.send(formData);
}
document.addEventListener("DOMContentLoaded", function () {
    const alert = document.querySelector('.error-alert');
    if (alert) {
        setTimeout(() => {
            alert.classList.add('fade-out');

            // 실제로 DOM에서 제거하려면 (선택)
            setTimeout(() => {
                alert.remove();
            }, 600); // transition 시간보다 약간 여유 있게
        }, 2000);
    }
});

