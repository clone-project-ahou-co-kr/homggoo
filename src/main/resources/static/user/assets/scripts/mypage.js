const $deleteBtn = document.querySelector('.delete');
const $mypageForm = document.getElementById('mypage-form');
const $mainContent = document.getElementById('main-content');
const $myContentContainer = $mainContent?.querySelector(':scope > .mypage-container > .view-category > .my');
const $buyContentContainer = $mainContent?.querySelector(':scope > .mypage-container > .view-category > .buy');
const $moreMyButton = $myContentContainer?.querySelector(':scope > .product-container > .more-box.myproduct-more > label > .my-more');
const $moreBuyButton = $buyContentContainer?.querySelector(':scope > .product-container > .more-box.buyproduct-more > label > .buy-more');

if ($moreMyButton) {
    $moreMyButton.addEventListener('click', () => {
        location.href = `${origin}/user/myproduct`;
    });
}

if ($moreBuyButton) {
    $moreBuyButton.addEventListener('click', () => {
        location.href = `${origin}/user/buyproduct`;
    });
}

if ($deleteBtn && $mypageForm) {
    $deleteBtn.addEventListener('click', () => {
        const emailInput = $mypageForm.querySelector('#email');
        const providerInput = $mypageForm.querySelector('#providerType');

        if (!emailInput || !providerInput) {
            console.warn('email 또는 providerType input이 없습니다.');
            return;
        }

        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("email", emailInput.value);
        formData.append("providerType", providerInput.value);

        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) return;
            if (xhr.status < 200 || xhr.status >= 300) {
                dialog.showSimpleOk('경고', '요청중 오류');
                return;
            }
            const response = JSON.parse(xhr.responseText);
            switch (response.result) {
                case 'failure':
                    break;
                case 'success':
                    break;
                default:
                    break;
            }
        };
        xhr.open('DELETE', 'api/user/mypage');
        xhr.send(formData);
    });
}