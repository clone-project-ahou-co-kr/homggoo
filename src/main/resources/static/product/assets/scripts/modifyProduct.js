const placeholder = document.getElementById('imagePlaceholder');

document.addEventListener("DOMContentLoaded", () => {
    const previewImg = document.getElementById('imagePreview');
    const imageBox = document.getElementById('imageBox');
    const imageInput = document.getElementById('productImage');
    const removeBtn = document.getElementById('removeImageBtn');
    const placeholder = document.getElementById('imagePlaceholder');

    // 최초 로딩 시 이미지 있으면 보여주기
    const defaultImageUrl = previewImg.dataset.image;
    if (defaultImageUrl) {
        previewImg.src = defaultImageUrl;
        previewImg.style.display = 'block';
        removeBtn.style.display = 'block';
        placeholder.style.display = 'none';
    }

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
            placeholder.style.display = 'none';
            removeBtn.style.display = 'block';
        };
        reader.readAsDataURL(file);
    });

    removeBtn.addEventListener('click', function (e) {
        e.stopPropagation(); // input 열리지 않도록
        previewImg.src = '';
        previewImg.style.display = 'none';
        removeBtn.style.display = 'none';
        placeholder.style.display = 'block';
        imageInput.value = ''; // 파일 초기화
    });
});

const $main = document.getElementById('main');
const $button = document.getElementById('uploadButton');
const $writeForm = document.getElementById('writeForm');
const $title = $main.querySelector('input[name="title"]');
const content = $writeForm.querySelector(':scope > .content > .main-content > .text-container > .custom-placeholder');
const price = $writeForm.querySelector(':scope > .content > .main-content > .text-container > .custom-placeholder.price');
const titleRegex = new RegExp('^(.{1,60})$');
const contentRegex = new RegExp('^.{1,100000}$', 's');
const priceRegex = /^[0-9]+$/;
const imageInput = document.getElementById('productImage');
const productData = document.getElementById('productData');
const ownerEmail = productData.dataset.ownerEmail;
const loggedEmail = productData.dataset.loggedEmail;

$button.addEventListener('click', () => {
    if (ownerEmail !== loggedEmail) {
        dialog.showSimpleOk('권한 없음', '상품을 수정할 권한이 없습니다.');
        return;
    }

    if ($writeForm['title'].value === '') {
        dialog.showSimpleOk('상품 수정', '제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (!titleRegex.test($writeForm['title'].value)) {
        dialog.showSimpleOk('상품 수정', '올바른 제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (content.textContent === '') {
        dialog.showSimpleOk('상품 수정', '내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }
    if (!contentRegex.test(content.textContent)) {
        dialog.showSimpleOk('상품 수정', '올바른 내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }if (price.textContent === '') {
        dialog.showSimpleOk('상품 수정', '가격을 입력해주세요.', {
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
    const url = new URL(location.href);
    const id = url.searchParams.get("id");
    formData.append('description', content.textContent);
    formData.append('price', price.textContent);
    formData.append('title', $writeForm['title'].value);
    formData.append('id', id);
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
                dialog.showSimpleOk('게시글 등록', '게시글이 수정되었습니다.', {
                    onOkCallback : () => location.href = `/product/production?category=${response.categoryId}&id=${response.id}`
                });
                break;
            case 'failure_unauthorized':
                dialog.showSimpleOk('상품 수정', '상품을 수정할 권한이 없습니다.')
                break;
            default:
                dialog.showSimpleOk('게시글 등록', '알 수 없는 이유로 게시글을 수정하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH','/api/posts/modifyProduct');
    xhr.send(formData);
})