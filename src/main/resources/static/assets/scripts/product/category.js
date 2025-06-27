const $listTable = document.getElementById('list');
const $listButton = $listTable.querySelector(':scope > .item > div > .btn');
const $listOpen = $listTable.querySelector(':scope > .item > .open');
let open = 0;
$listButton.addEventListener('click', () => {
    if (open === 0) {
        $listOpen.classList.add('open-list');
        open += 1;
    } else if (open <= 1) {
        $listOpen.classList.remove('open-list');
        open -= 1;
    }
})