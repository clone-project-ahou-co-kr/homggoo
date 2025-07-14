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

$delete.addEventListener('click', () => {
    alert('!@!!!');
});