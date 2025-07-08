const $main =document.getElementById('main-production');
const $menuBar = $main.querySelector(':scope > .explain-section > .menu-bar');
const $menuOpen = $menuBar.querySelector(':scope > .click-button');
const $open = $menuBar.querySelector(':scope > .click-button > .icon');
const $menuList = $menuBar.querySelector(':scope > .menu-list')
const $modify = $menuBar.querySelector(':scope > .menu-list > .list > .item > .button[name="modify"]');
const $delete = $menuBar.querySelector(':scope > .menu-list > .list > .item > .button[name="delete"]');

$menuOpen.addEventListener('click', () => {
    if ($open.classList.contains('open')) {
        $open.classList.remove('open');
        $menuList.classList.remove('visible');
    } else {
        $open.classList.add('open');
        $menuList.classList.add('visible');
    }
});

$modify.addEventListener('click', () => {
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

            return;
        }

    };
    xhr.open('PATCH', );
    xhr.send(formData);
});

$delete.addEventListener('click', () => {
    alert('!@!!!');
});