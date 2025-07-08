const $listTable = document.getElementById('list');
const $mainSection = document.getElementById('main-category');
const filterClassList = [
    'color', 'wood', 'ingredient', 'category', 'personnel', 'ripper',
    'shop', 'brand', 'special', 'today', 'only', 'price', 'delivery'
];
const $filters = {};
const $filterButtons = {};
const $dropdowns = {};
const openStates = {};

$listTable.addEventListener('click', (e) => {
    const $targetBtn = e.target.closest('button');
    if (!$targetBtn) return;

    const $item = $targetBtn.closest('.item');

    // 1. .list-btn 안의 버튼 클릭 시 .open-box 열기 (다른 open은 닫기)
    const $openBox = $item.querySelector(':scope > .open-box');
    if ($openBox && $targetBtn.closest('.list-btn')) {
        // 다른 모든 .open-box와 그 내부 .open.visible 닫기
        const $allItems = $listTable.querySelectorAll('.item');
        $allItems.forEach(($li) => {
            const $box = $li.querySelector(':scope > .open-box');
            if ($box && $box !== $openBox) {
                $box.classList.remove('open');
                // 내부의 .open.visible도 닫기
                const $innerOpens = $box.querySelectorAll('.open.visible');
                $innerOpens.forEach($el => {
                    $el.classList.remove('visible');
                });
            }
        });

        // 클릭한 것만 토글
        $openBox.classList.toggle('open');
        return;
    }

    // 2. .open-box 내부 버튼 클릭 시, 다음 형제(open)에 visible 클래스 토글
    const $subWrapper = $targetBtn.closest('.open-box');
    if ($subWrapper) {
        const $nestedOpen = $targetBtn.parentElement.nextElementSibling;
        if ($nestedOpen && $nestedOpen.classList.contains('open')) {
            $nestedOpen.classList.toggle('visible');
        }
    }
});


filterClassList.forEach((className) => {
    const $filterItem = $mainSection.querySelector(`.filter-items > .filter > ul > .${className}`);
    const $button = $filterItem?.querySelector('button');
    const $dropdown = $filterItem?.querySelector(`.dropdown-${className}`);

    $filters[className] = $filterItem;
    $filterButtons[className] = $button;
    $dropdowns[className] = $dropdown;
    openStates[className] = false;

    if ($button && $dropdown) {

        $button.addEventListener('click', () => {
            if (!openStates[className]) {
                $dropdown.classList.add('open');
                $button.classList.add('open');
                openStates[className] = true;
            } else {
                $dropdown.classList.remove('open');
                $button.classList.remove('open');
                openStates[className] = false;
            }
        });
    }
});