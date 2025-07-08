const $mainContent = document.getElementById('main-content');

const $menuContainer = $mainContent.querySelector(':scope>.info-section>.inner-container>.info-container>.menu-container');
const $list = $menuContainer.querySelector(':scope>.notice-container>.list');
const loadNotice = () => {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청중 오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        $list.innerHTML = '';
        const notices = response.notices;
        console.log(response.notices);
        notices.forEach((notice) => {
            const item = `
                        <li class="item" data-index="${notice.index}">
                            <span> ${notice.title} </span>
                            <span> ${notice.nickname} </span>
                            <span> ${notice.createdAt} </span>
                        </li>
            `;
            $list.insertAdjacentHTML('beforeend', item);
        })
        const items = $list.querySelectorAll(':scope>.item');
        items.forEach((item) => {
            item.addEventListener('click', () => {
                const index = item.dataset['index'];
                const xhr = new XMLHttpRequest();

                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                        return;
                    }
                    if (xhr.status < 200 || xhr.status >= 300) {
                        dialog.showSimpleOk('경고', '요청중 오류')
                        return;
                    }
                    location.href = `${origin}/experts/notice/specific?index=${index}`;
                };
                xhr.open('GET', `/experts/notice/specific?index=${index}`);
                xhr.send();
            })
            item.addEventListener('click',()=>{
                const index = item.dataset['index'];
                const xhr = new XMLHttpRequest();
                const formData = new FormData();
                formData.append('index', index);
                xhr.onreadystatechange=()=>{
                    if(xhr.readyState !== XMLHttpRequest.DONE){
                        return;
                    }
                    if (xhr.status < 200 || xhr.status >= 300){
                        dialog.showSimpleOk('경고', '요청중 오류')
                        return;
                    }
                    const response = JSON.parse(xhr.responseText);
                    switch (response.result) {
                        case 'failure':
                            dialog.showSimpleOk('조회수', '실패');
                            break;
                        case'success':
                            dialog.showSimpleOk('조회수', '성공');
                            break;
                        default:
                            break;
                    }
                };
                xhr.open('POST',`/api/expert/notice`);
                xhr.send(formData);
            })
        })

    };
    xhr.open('GET', '/api/expert/notice');
    xhr.send();
}


window.addEventListener('DOMContentLoaded', () => {
    loadNotice();
})