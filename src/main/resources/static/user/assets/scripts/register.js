const $registerForm = document.getElementById('register-form');
const emailId = $registerForm.querySelector('input[name="email"]').value.trim();
const emailDomainSelect = $registerForm.querySelector('select[name="email-address"]');
const emailDomain = emailDomainSelect.value;
const email = `${emailId}@${emailDomain}`;
const $loading = document.getElementById('loading');
const clientId = 'HRD9oEKvyHgE3W9QLwYs';
const callbackUrl = 'http://localhost:8080/';
const getFullEmail = ($form) => {
    const emailId = $form.querySelector('input[name="email"]').value.trim();
    const emailDomain = $form.querySelector('select[name="email-address"]').value.trim();
    if (!emailId || !emailDomain) return null; // 값이 비었을 때 대비
    return `${emailId}@${emailDomain}`;
}

$registerForm.onsubmit = (e) => {
    e.preventDefault();
    //1. 이메일 입력.
    const email = getFullEmail($registerForm);

    // 2. 비밀번호 입력
    const password = $registerForm.querySelector('input[name="password"]').value;
    const passwordCheck = $registerForm.querySelector('input[name="passwordCheck"]').value;

    // 3. 닉네임 입력
    const nickname = $registerForm.querySelector('input[name="nickname"]').value.trim();

    // 4. 체크박스 (예: 이용약관 동의 등)
    const isOver14 = $registerForm.querySelector('input[name="age"]').checked;
    const isAgreed = $registerForm.querySelector('input[name="agree"]').checked;

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);
    formData.append('nickname', nickname);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청 중 오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case'failure':
                alert('failure');
                break;
            case 'success':
                alert('success');
                break;
            default:
                break;
        }
    };
    xhr.open('POST', "/api/user/register");
    xhr.send(formData);
}
$registerForm['emailCodeSendButton'].addEventListener('click', () => {
    const email = getFullEmail($registerForm);
    if ($registerForm.querySelector('input[name="email"]').value === '') {
        $registerForm['email'].classList.add
        ('warning');
        alert('경고');
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", email);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 처리하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case'failure':
                alert('보내기 실패');
                break;
            case 'success':
                $registerForm['emailSalt'].value = response.salt;
                $registerForm.querySelector(':scope>.email-container>.email-verify').setVisible(true);
                break;
            default:
                break;
        }
    };
    xhr.open('POST', '/api/user/register-email');
    xhr.send(formData);
    // $loading.setVisible(true);
})

$registerForm['emailCodeVerifyButton'].addEventListener('click', () => {
    const email = getFullEmail($registerForm);
    if ($registerForm['emailCode'].value === '') {
        alert('입력해주세요.');
        $registerForm['emailCode'].focus();
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', email);
    formData.append('code', $registerForm['emailCode'].value);
    formData.append("salt", $registerForm['emailSalt'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청중 오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case'failure':
                alert('실패');
                break;
            case 'success':
                alert('성공');
                break;
            default:
                break;
        }
    };
    xhr.open('PATCH', '/api/user/register-email');
    xhr.send(formData);
})

document.getElementById('naver-login-btn').addEventListener('click', () => {
    document.getElementById('naver_id_login').querySelector('a').click();
})