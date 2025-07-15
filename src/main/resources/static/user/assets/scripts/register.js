const $registerForm = document.getElementById('register-form');
const emailRegex = /^[\da-zA-Z._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[`~!@#$%^&*()\-=+_[\]{}|;:'",.<>/?\\])[A-Za-z\d`~!@#$%^&*()\-=+_[\]{}|;:'",.<>/?\\]{8,50}$/;
const nicknameRegex = /^[가-힣a-zA-Z\d]{2,20}$/;
const $containers = $registerForm.querySelectorAll(':scope>.common>label>.input-object');
$containers.forEach(input => {
    input.setAttribute('disabled', '');
});
$registerForm['email'].removeAttribute('disabled');
$registerForm['email-address'].removeAttribute('disabled');


let timerInterval = null;
let timerSeconds = 180;
const startTimer = () => {
    clearInterval(timerInterval);
    timerSeconds = 180;

    const $timer = $registerForm.querySelector('[data-role="email-timer"]');

    if (!$timer) {
        return;
    }

    updateTimerDisplay($timer);
    timerInterval = setInterval(() => {
        timerSeconds--;
        updateTimerDisplay($timer);
        if (timerSeconds <= 0) {
            clearInterval(timerInterval);
            dialog.showSimpleOk('이메일 인증', '인증 시간이 만료되었습니다. 다시 시도해주세요.');
        }
    }, 1000)
}
const updateTimerDisplay = ($timer) => {
    const minutes = String(Math.floor(timerSeconds / 60)).padStart(1, '0');
    const seconds = String(timerSeconds % 60).padStart(2, '0');
    $timer.textContent = `${minutes}:${seconds}`;
}

$registerForm['emailCodeSendButton'].addEventListener('click', (e) => {
    e.preventDefault();

    const emailInput = $registerForm['email'];
    const domainSelect = $registerForm['email-address'];

    const emailCommonMsg = $registerForm.querySelector('[data-role="email-common"]');
    const emailErrorMsg = $registerForm.querySelector('[data-role="email-error"]');
    const emailVerifyContainer = $registerForm.querySelector('.email-verify');

    const emailLocal = emailInput.value.trim();
    const emailDomain = domainSelect.value;
    let email = '';

    const firstEmail = /^[\da-z\-_.]{4,}$/;
    const emailRegex = /^(?=.{8,50}$)([\da-z\-_.]+)@([\da-z][\da-z\-]*[.\w]+)$/;

    // 초기 메시지 숨김
    emailCommonMsg.style.display = 'none';
    emailErrorMsg.style.display = 'none';

    // 1. 이메일 앞부분 입력 안 했을 때
    if (emailLocal === '') {
        emailCommonMsg.style.display = 'flex';
        emailInput.focus();
        return;
    }

    // 2. 도메인 선택 안 했을 때
    if (emailDomain === '') {
        emailErrorMsg.textContent = '이메일 도메인을 선택해주세요.';
        emailErrorMsg.style.display = 'flex';
        domainSelect.focus();
        return;
    }

    // 3. 직접입력(by)이면 전체 이메일 형식 체크
    if (emailDomain === 'by') {
        if (!emailRegex.test(emailLocal)) {
            dialog.showSimpleOk('회원가입', '이메일 형식이 올바르지 않습니다.');
            emailInput.focus();
            return;
        }
        email = emailLocal;
    } else {
        email = `${emailLocal}@${emailDomain}`;
        if (!emailRegex.test(email)) {
            emailErrorMsg.textContent = '올바른 이메일 형식이 아닙니다.';
            emailErrorMsg.style.display = 'flex';
            return;
        }
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", email);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청 중 오류가 발생했습니다.');
            return;
        }

        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'failure':
                dialog.showSimpleOk('인증번호 전송', `입력하신 이메일 '${email}'은(는) 이미 사용 중입니다.`);
                break;
            case 'success':
                $registerForm['emailSalt'].value = response.salt;
                startTimer();
                emailVerifyContainer.setVisible(true);
                break;
        }
    };

    xhr.open('POST', '/api/user/register-email');
    xhr.send(formData);
});


$registerForm['emailCodeVerifyButton'].addEventListener('click', () => {
    const emailLocal = $registerForm['email'].value.trim();
    const emailDomain = $registerForm['email-address'].value;
    const emailCode = $registerForm['emailCode'].value.trim();
    const emailSalt = $registerForm['emailSalt'].value;
    const emailVerifyContainer = $registerForm.querySelector('.email-verify');

    let email = '';
    if (emailDomain === 'by') {
        email = emailLocal;
        $registerForm['email-address'].setDisabled?.(true);  // 유틸 함수 사용 시
    } else {
        email = `${emailLocal}@${emailDomain}`;
    }

    if (emailCode === '') {
        dialog.showSimpleOk('이메일 인증', '인증코드를 입력해주세요.');
        $registerForm['emailCode'].focus();
        return;
    }

    // 서버 요청
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', email);
    formData.append('code', emailCode);
    formData.append('salt', emailSalt);

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            alert('요청 중 오류가 발생했습니다.');
            return;
        }

        const response = JSON.parse(xhr.responseText);

        switch (response.result) {
            case 'failure':
                dialog.showSimpleOk('이메일 인증', '인증번호가 틀렸습니다. 다시 시도해주세요.');
                break;
            case 'success':
                clearInterval(timerInterval);
                dialog.showSimpleOk('이메일 인증', '인증에 성공했습니다!');
                emailVerifyContainer.setVisible?.(false);  // 너가 쓰는 커스텀 유틸
                $registerForm['password'].removeAttribute('disabled');
                break;
            default:
                alert('예상치 못한 오류가 발생했습니다.');
                break;
        }
    };

    xhr.open('PATCH', '/api/user/register-email');
    xhr.send(formData);
});

$registerForm['password'].addEventListener('focusout', () => {
    const passwordInput = $registerForm['password'];
    const errorMsg = $registerForm.querySelector('[data-role="password-error"]');
    const commonMsg = $registerForm.querySelector('[data-role="password-common"]')

    errorMsg.style.display = "none";
    commonMsg.style.display = "none";

    // 1. 입력값이 비어있으면 common 메시지
    if (passwordInput.value === '') {
        commonMsg.style.display = "flex";
        return;
    }

    // 2. HTML5 패턴 체크에 실패한 경우 error 메시지
    if (!passwordInput.validity.valid) {
        errorMsg.style.display = "flex";
        return;
    }

    // 3. 성공한 경우 -> 에러 메시지 숨김
    $registerForm['passwordCheck'].removeAttribute('disabled');
});

$registerForm['passwordCheck'].addEventListener('focusout', () => {
    $registerForm.querySelector('[data-role="passwordCheck-error"]').style.display = "none";
    $registerForm.querySelector('[data-role="passwordCheck-common"]').style.display = "none";
    if ($registerForm['passwordCheck'].value === '') {
        $registerForm.querySelector('[data-role="passwordChcek-common"]').style.display
            = "flex";
        return;
    }
    if ($registerForm['passwordCheck'].value !== $registerForm['password'].value) {
        $registerForm.querySelector('[data-role="passwordCheck-error"]').style.display
            = "flex";
        return;
    }
    $registerForm['nickname'].removeAttribute('disabled');
})
$registerForm['nickname'].addEventListener('focusout', () => {
    $registerForm.querySelectorAll(':scope>.nickname-container>[data-message="message"]').forEach(message => message.style.display = 'none');
    if ($registerForm['nickname'].value === '') {
        $registerForm.querySelector('[data-role="nickname-common"]'
        ).style.display = 'flex';
        return;
    } else if (!$registerForm['nickname'].validity.valid) {
        $registerForm.querySelector('[data-role="nickname-error"]').style.display = 'flex';
        return;
    }
})

$registerForm.onsubmit = (e) => {
    e.preventDefault();
    let email;
    $registerForm.querySelectorAll('[data-message="message"]').forEach(message => message.style.display = 'none');
    //email 인증
    if ($registerForm['email'].value === '') {
        $registerForm['email'].select();
        $registerForm['email'].focus();
        $registerForm.querySelector('[data-role="email-common"]').style.display = 'flex';
        return;
    }
    if ($registerForm['email-address'].value === 'by') {
        if (!emailRegex.test($registerForm['email'].value)) {
            $registerForm.querySelector('[data-role="email-error"]').style.display = "flex";
            return;
        }
        email = $registerForm['email'].value;

    } else {
        email = `${$registerForm['email'].value}@${$registerForm['email-address'].value}`;
    }
    if ($registerForm['password'].value === '') {
        $registerForm.querySelector('[data-role="password-common"]').style.display = "flex";
        $registerForm['password'].select();
        $registerForm['password'].focus();
        return;
    }
    if (!passwordRegex.test($registerForm['password'].value)) {
        $registerForm.querySelector('[data-role="password-error"]').style.display = "flex";
        $registerForm['password'].select();
        return;
    }
    if ($registerForm['password'].value !== $registerForm['passwordCheck'].value) {
        $registerForm.querySelector('[data-role="passwordCheck-error"]').style.display = "flex";
        return;
    }
    if ($registerForm['nickname'].value === '') {
        $registerForm.querySelector('[data-role="nickname-common"]').style.display = "flex";
        $registerForm['nickname'].focus();
        $registerForm['nickname'].select();
        return;
    }
    if (!nicknameRegex.test($registerForm['nickname'].value)) {
        $registerForm.querySelector('[data-role="nickname-error"]').style.display = "flex";
        $registerForm['nickname'].select();
        return;
    }

    if (!$registerForm['age'].checked || !$registerForm['agree'].checked) {
        $registerForm.querySelector('[data-role="agree-common"]').style.display = "flex";
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', $registerForm['password'].value);
    formData.append('nickname', $registerForm['nickname'].value);
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
                location.href = `${origin}/`;
                break;
            default:
                break;
        }
    };
    xhr.open('POST', "/api/user/register");
    xhr.send(formData);
}
document.addEventListener('DOMContentLoaded', () => {
    const $registerForm = document.getElementById('register-form');
    const $selectAll = $registerForm.querySelector('input[name="select-all"]');
    const $agreements = $registerForm.querySelectorAll('input.all');

    // 전체 동의 체크 시 -> 나머지 체크박스 체크
    $selectAll.addEventListener('change', () => {
        const isChecked = $selectAll.checked;
        $agreements.forEach(checkbox => checkbox.checked = isChecked);
    });

    // 개별 체크박스 체크 변경 시 -> 전체 동의 상태 동기화
    $agreements.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            const allChecked = Array.from($agreements).every(checkbox => checkbox.checked);
            $selectAll.checked = allChecked;
        });
    });
});
