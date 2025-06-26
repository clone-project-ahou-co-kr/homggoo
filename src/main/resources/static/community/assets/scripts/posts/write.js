const $main = document.getElementById('main');
const $title = $main.querySelector(':scope > #writeForm > .title-container > .label > input[name="title"]')
const content = $main.querySelector(':scope > #writeForm > .text-container > .custom-placeholder');
const $subject = document.getElementById('subject');
const $posts = document.getElementById('posts');

const $header = document.getElementById('header');
const $button = $header.querySelector(':scope > .header-container > .button-container > button');

const optionsMap = {
    "subject": [],
    "honey": ['집요한생일', '살까말까', '상품후기', '꿀잼수다'],
    "decoration": ['홈스타일링', '시공/모델링', '우리집일상']
}


$subject.addEventListener('change', () => {
    const selected = $subject.value;
    $posts.innerHTML = '';
    console.log(content)
    if (optionsMap[selected]) {
        optionsMap[selected].forEach(text => {
            const option = document.createElement('option');
            $subject.style.color = "black";
            $posts.style.color = "black";
            option.textContent = text;

            $posts.appendChild(option);
        });
    }

    if (content.textContent != null ||
        $title.textContent != null ||
        $subject.value != null ||
        $posts.value != null) {

    }

});