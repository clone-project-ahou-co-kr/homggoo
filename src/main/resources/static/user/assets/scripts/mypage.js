const $deleteBtn = document.body.querySelector(':scope>.delete');
const $mypageForm = document.getElementById('mypage-form');
const $mainContent = document.getElementById('main-content');
const $contentContainer = $mainContent.querySelector(':scope > .mypage-container > .content-container');
const $moreButton = $contentContainer.querySelector(':scope > .product-container > .more-box > label > .more');

$moreButton.addEventListener('click', () => {
    location.href = `${origin}/user/myproduct`
})

$deleteBtn.addEventListener('click',()=>{
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", $mypageForm.getElementById('email').value);
    formData.append("providerType", $mypageForm.getElementById('providerType').value);
    xhr.onreadystatechange=()=>{
        if(xhr.readyState !== XMLHttpRequest.DONE){
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300){
            dialog.showSimpleOk('경고', '요청중 오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case'failure':
                break;
            case'success':
                break;
            default:
                break;
        }
    };
    xhr.open('DELETE','api/user/mypage');
    xhr.send(formData);
})