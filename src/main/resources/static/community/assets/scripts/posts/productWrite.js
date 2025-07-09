const $main = document.getElementById('main');
const $header = document.getElementById('header');
const $button = document.getElementById('uploadButton');
const $writeForm = document.getElementById('writeForm');
const $title = $main.querySelector('input[name="title"]');
const content = $writeForm.querySelector(':scope > .content > .main-content > .text-container > .custom-placeholder');
const price = $writeForm.querySelector(':scope > .content > .main-content > .text-container > .custom-placeholder.price');
const titleRegex = new RegExp('^(.{1,60})$');
const contentRegex = new RegExp('^.{1,100000}$', 's');
const imageInput = document.getElementById('productImage');
const imageBox = document.getElementById('imageBox');
const previewImg = document.getElementById('imagePreview');

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
    if ($writeForm['title'].value === '') {
        dialog.showSimpleOk('게시글 작성', '제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (!titleRegex.test($writeForm['title'].value)) {
        dialog.showSimpleOk('게시글 작성', '올바른 제목을 입력해주세요.', {
            onOkCallback: () => $writeForm['title'].focus()
        });
        return;
    }
    if (content.textContent === '') {
        dialog.showSimpleOk('게시글 작성', '내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }
    if (!contentRegex.test(content.textContent)) {
        dialog.showSimpleOk('게시글 작성', '올바른 내용을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }if (price.textContent === '') {
        dialog.showSimpleOk('게시글 작성', '가격을 입력해주세요.', {
            onOkCallback: () => content.focus()
        });
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('description', content.textContent);
    formData.append('price', price.textContent);
    formData.append('title', $writeForm['title'].value);
    formData.append('productId', $writeForm['productId'].value);
    console.log(imageInput.files[0]);
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
                dialog.showSimpleOk('게시글 등록', '게시글이 등록되었습니다.', {
                    onOkCallback : () => location.href = `/product/production?id=${response.id}`
                });
                break;
            default:
                dialog.showSimpleOk('게시글 등록', '알 수 없는 이유로 게시글을 등록하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('POST','/api/posts/newProduct');
    xhr.send(formData);
})

