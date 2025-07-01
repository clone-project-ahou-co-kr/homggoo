const $registerForm = document.getElementById('register-form');
const emailId = $registerForm.querySelector('input[name="email"]').value.trim();
const emailRegex = new RegExp('^(?=.{8,50}$)([\\da-z\\-_.]{4,})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2,3})?$');
const passwordRegex = new RegExp('^([\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:\'",<.>/?]{8,50})$');
const emailCodeRegex = new RegExp('^(\\d{6})$');
const nicknameRegex = new RegExp('^([\\da-zA-Z가-힣]{2,10})$');
const nameRegex = new RegExp('^([가-힣]{2,5})$');
$registerForm['emailCodeSendButton'].addEventListener('click', () => {
    let email = '';
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if ($registerForm['email'].value === '') {
        alert('이메일을 입력해주세요.');
        $registerForm['email'].focus();
        $registerForm.querySelector(':scope>.email-container>.label-object:has(input[name="email"]) ~ .error-message').style.display = "none";
        $registerForm.querySelector(':scope>.email-container>.label-object:has(input[name="email"]) + .common-message').style.display = "flex";
        return;
    }

    if ($registerForm['email-address'].value === '') {
        alert('도메인을 선택해주세요.');
        $registerForm['email-address'].focus();
        $registerForm.querySelector(':scope>.email-container>.label-object:has(input[name="email"]) + .common-message').style.display = "none";
        $registerForm.querySelector(':scope>.email-container>.label-object:has(input[name="email"]) ~ .error-message').style.display = "flex";
        return;
    }

    if ($registerForm['email-address'].value === 'by') {
        // 사용자가 전체 이메일을 직접 입력했다고 가정
        email = $registerForm['email'].value;
    } else {
        // 조합
        email = `${$registerForm['email'].value}@${$registerForm['email-address'].value}`;
    }

    if (!emailRegex.test(email)) {
        alert('올바른 이메일 형식이 아닙니다.');
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", email);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청을 처리하는 도중 오류가 발생했습니다.');
            return;
        }

        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'failure':
                alert('이메일 전송 실패');
                break;
            case 'success':
                $registerForm['emailSalt'].value = response.salt;
                $registerForm.querySelector(':scope > .email-container > .email-verify').setVisible(true);
                break;
        }
    };

    xhr.open('POST', '/api/user/register-email');
    xhr.send(formData);
});


$registerForm['emailCodeVerifyButton'].addEventListener('click', () => {
    let email;
    if ($registerForm['email-address'].value === 'by') {
        $registerForm['email-address'].setDisabled(true);
        email = $registerForm['email'].value;
    } else {
        email = `${$registerForm['email'].value}@${$registerForm['email-address'].value}`
    }
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
                $registerForm['email-verify'].setVisible(false);
                break;
            default:
                break;
        }
    };
    xhr.open('PATCH', '/api/user/register-email');
    xhr.send(formData);
})
$registerForm.onsubmit = (e) => {
    e.preventDefault();
    //1. 이메일 입력.
    let email;
    if ($registerForm['email-address'].value === 'by') {
        // 사용자가 전체 이메일을 직접 입력했다고 가정
        email = $registerForm['email'].value;
        $registerForm['email-address'].setDisabled(true);
    } else {
        // 조합
        email = `${$registerForm['email'].value}@${$registerForm['email-address'].value}`;
    }
    // 2. 비밀번호 입력
    const password = $registerForm.querySelector('input[name="password"]').value;
    const passwordCheck = $registerForm.querySelector('input[name="passwordCheck"]').value;
    if (password === '') {
        alert('비밀번호를 입력해주세요.');
        return;
    }
    if (passwordCheck === '') {
        alert('확인을위해 비밀번호 입력 ㄱ');
        return;
    }
    // 3. 닉네임 입력
    const nickname = $registerForm.querySelector('input[name="nickname"]').value.trim();
    if (nickname === '') {
        alert('닉네임을 입력해 주세요.');
        return;
    }

    if ($registerForm['email'].value === '') {
        $registerForm.querySelector(':scope>.email-container>.label-object + .common-message').style.display = "flex";
    }
    if ($registerForm['email'].value === '') {
        $registerForm.querySelector(':scope>.email-container>.label-object + .common-message').style.display = "flex";
    }
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


document.getElementById('naver-login-btn').addEventListener('click', () => {
    document.getElementById('naver_id_login').querySelector('a').click();
})