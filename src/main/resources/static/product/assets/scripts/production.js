// import el from "../../../assets/libraries/ckeditor5/translations/el";

const $main = document.getElementById('main-production');
const $menuBar = $main.querySelector(':scope > .explain-section > .menu-bar');
let $menuOpen, $open, $menuList, $delete;
if ($menuBar) {
    $menuOpen = $menuBar.querySelector(':scope > .menu > .click-button');
    $open = $menuBar.querySelector(':scope > .menu > .click-button > .dot-container');
    $menuList = $menuBar.querySelector(':scope > .menu-list');
    $delete = $menuList.querySelector(':scope > .list > .item > .button[name="delete"]');
}
const $favorite = $main.querySelector(':scope > .explain-section > .explain > .pay > .button-container > .favorites > .favorite-button');
const $favoriteSvg = $favorite.querySelector(':scope > svg');
const productData = document.getElementById('productData');
const ownerEmail = productData.dataset.ownerEmail;
const loggedEmail = productData.dataset.loggedEmail;

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

if ($menuBar != null) {
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
        if (ownerEmail !== loggedEmail) {
            dialog.showSimpleOk('권한 없음', '상품을 수정할 권한이 없습니다.');
            return;
        }
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
                                case 'failure_unauthorized':
                                    dialog.showSimpleOk('상품 삭제', '상품을 삭제할 권한이 없습니다.')
                                    break;
                                default:
                                    dialog.showSimpleOk('상품 삭제', '알 수 없는 이유로 상품을 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                            }
                        };
                        xhr.open('DELETE', '/api/posts/deleteProduct');
                        xhr.send(formData);
                    }
                }
            ]
        })
    });
}

const $payButton = $main.querySelector(':scope > .explain-section > .explain > .pay > .button-container > button:last-child');

$payButton.addEventListener('click', () => {
    if (loggedEmail === ownerEmail) {
        dialog.showSimpleOk('상품 구매', '상품을 구매 할 수 없습니다.')
        return;
    }
    if (loggedEmail === '' || loggedEmail == null) {
        dialog.showSimpleOk('상품 구매', '상품을 구매하기 위해선 로그인이 필요합니다.', {
            onOkCallback: () => location.href = `${origin}/user/login`
        });
        return;
    }
    dialog.show({
        title: '결재하기',
        content: '정말로 상품을 결재할까요?',
        buttons: [
            {
                caption: '취소',
                onclick: ($modal) => dialog.hide($modal)
            },
            {
                caption: '구매하기',
                color: 'green',
                onclick: ($modal) => {
                    dialog.hide($modal);
                    const imp = window.IMP;
                    imp.init('imp73305074'); // PortOne 가맹점 식별코드

                    const url = new URL(location.href);
                    const productId = url.searchParams.get("id");
                    const productTitle = $main.querySelector(':scope .title').innerText;
                    const productPriceText = $main.querySelector(':scope .price').innerText;
                    const productPrice = parseInt(productPriceText.replace(/[^0-9]/g, ''), 10); // "10,000원" → 10000

                    imp.request_pay({
                        pg: 'kakaopay.TC0ONETIME',
                        pay_method: 'card',
                        merchant_uid: `IMP-${Date.now()}`,
                        name: productTitle,
                        amount: productPrice,
                        buyer_email: 'dbswjdgus94@naver.com',
                        buyer_name: '윤정현'
                    }, (resp) => {
                        if (resp.success === true) {
                            dialog.showSimpleOk('상품 결제', '상품 결제완료!');
                            location.reload();
                        } else {
                            dialog.showSimpleOk('상품 결제', `${resp.error_msg}`);
                        }
                    });
                }
            }
        ]
    })
});