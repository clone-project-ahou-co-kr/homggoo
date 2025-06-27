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