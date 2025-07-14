// import el from "../../../assets/libraries/ckeditor5/translations/el";

const $main = document.getElementById('main-production');
const $menuBar = $main.querySelector(':scope > .explain-section > .menu-bar');
const $menuOpen = $menuBar.querySelector(':scope > .menu > .click-button');
const $open = $menuBar.querySelector(':scope > .menu > .click-button > .dot-container');
const $menuList = $menuBar.querySelector(':scope > .menu  > .menu-list')
const $delete = $menuList.querySelector(':scope > .list > .item > .button[name="delete"]');
const $favorite = $main.querySelector(':scope > .explain-section > .explain > .pay > .button-container > .favorites > .favorite-button');
const $favoriteSvg = $favorite.querySelector(':scope > svg');

$favorite.addEventListener('click', (e) => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    const url = new URL(location.href);
    const id = url.searchParams.get("id");
    formData.append("id", id);
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
        if (response.result === true) {
            $favoriteSvg.classList.add('click');
            location.reload();
        } else if (response.result === false) {
            $favoriteSvg.classList.remove('click');
            location.reload();
        } else {
            dialog.showSimpleOk('경고', '요청을 처리할 수 없습니다. 세션이 만료되었거나 음원이 삭제되었을 수 있습니다.');
        }
    };
    xhr.open('PATCH', '/api/posts/productLike');
    xhr.send(formData);
});

$menuOpen.addEventListener('click', () => {
    if ($open.classList.contains('open')) {
        $open.classList.remove('open');
        $menuList.classList.remove('visible');
    } else {
        $open.classList.add('open');
        $menuList.classList.add('visible');
    }
});

$delete.addEventListener('click', () => {
    dialog.show({
        title: '상품 삭제',
        content: '정말로 상품을 삭제할까요? 삭제된 상품은 복구가 어렵습니다.',
        buttons: [
            {
                caption: '취소',
                onclick: ($modal) => dialog.hide($modal)
            },
            {
                caption: '삭제',
                color: 'red',
                onclick: ($modal) => {
                    dialog.hide($modal);
                    const xhr = new XMLHttpRequest();
                    const formData = new FormData();
                    const url = new URL(location.href);
                    const id = url.searchParams.get("id");
                    formData.append("id", id);
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
                        switch (response.result) {
                            case 'success':
                                dialog.showSimpleOk('상품 삭제', '상품이 삭제되었습니다.', {
                                    onOkCallback: () => location.href = '/product/all'
                                });
                                break;
                            default:
                                dialog.showSimpleOk('상품 삭제', '알 수 없는 이유로 게시글을 등록하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                        }
                    };
                    xhr.open('DELETE', '/api/posts/deleteProduct');
                    xhr.send(formData);
                }
            }
        ]
    })
});