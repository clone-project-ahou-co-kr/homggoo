const $main = document.getElementById('main');
const $button = document.getElementById('uploadButton');
const $subject = document.getElementById('subject');
const $writeForm = document.getElementById('writeForm');
const content = $writeForm.querySelector(':scope > .content > .main-content > .text-container > .custom-placeholder');
const price = $writeForm.querySelector(':scope > .content > .main-content > .text-container > .custom-placeholder.price');
const titleRegex = new RegExp('^(.{1,60})$');
const priceRegex = /^[0-9]+$/;
const contentRegex = new RegExp('^.{1,100000}$', 's');
const imageInput = document.getElementById('productImage');
const imageBox = document.getElementById('imageBox');
const previewImg = document.getElementById('imagePreview');

document.addEventListener("DOMContentLoaded", function () {
    const selectElement = document.querySelector("#subject");

    selectElement.addEventListener("change", function () {
        if (this.value) {
            this.classList.add("selected");
        } else {
            this.classList.remove("selected");
        }
    });
});

imageBox.addEventListener('click', () => {
    imageInput.click();
});

imageInput.addEventListener('change', function () {
    const file = this.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function (e) {
        previewImg.src = e.target.result;
        previewImg.style.display = 'block';
        imageBox.querySelector('span').style.display = 'none';
    };
    reader.readAsDataURL(file);
});

$button.addEventListener('click', () => {
    if ($subject.value === '종류를 선택해주세요(필수)') {
        dialog.showSimpleOk('상품 등록', '가구 종류를 선택해주세요', {
            onOkCallback: () => $subject.focus()
        });
        return;
    }
    if ($writeForm['title'].value === '') {
        dialog.showSimpleOk('상품 등록', '제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (!titleRegex.test($writeForm['title'].value)) {
        dialog.showSimpleOk('상품 등록', '올바른 제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (content.textContent === '') {
        dialog.showSimpleOk('상품 등록', '내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }
    if (!contentRegex.test(content.textContent)) {
        dialog.showSimpleOk('상품 등록', '올바른 내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }if (price.textContent === '') {
        dialog.showSimpleOk('상품 등록', '가격을 입력해주세요.', {
            onOkCallback: () => price.focus()
        });
        return;
    }
    if (!priceRegex.test(price.textContent)) {
        dialog.showSimpleOk('상품 등록', '가격을 숫자로만 입력해주세요.',{
            onOkCallback: () => price.focus()
        })
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('description', content.textContent);
    formData.append('price', price.textContent);
    formData.append('title', $writeForm['title'].value);
    formData.append('productId', $writeForm['productId'].value);
    formData.append('categoryCode', $subject.value);
    formData.append('_image', imageInput.files[0]);

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
        console.log(response)
        switch (response.result) {
            case 'success':
                dialog.showSimpleOk('상품 등록', '상품이 등록되었습니다.', {
                    onOkCallback : () => location.href = `/product/production?category=${response.categoryId}&id=${response.id}`
                });
                break;
            case 'failure_absent':
                dialog.showSimpleOk('상품 등록', '로그인이 필요합니다.', {
                    onOkCallback : () => location.href = `/user/login`
                })
                break;
            default:
                dialog.showSimpleOk('상품 등록', '알 수 없는 이유로 상품을 등록하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('POST','/api/posts/newProduct');
    xhr.send(formData);
})

