const $mainContent = document.getElementById('main-content');

const $menuContainer = $mainContent.querySelector(
    ':scope > .info-section > .inner-container > .info-container > .menu-container'
);

const $table = $menuContainer.querySelector(':scope > .notice-container > table');

const loadNotice = () => {
    const xhr = new XMLHttpRequest();

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청중 오류');
            return;
        }

        const response = JSON.parse(xhr.responseText);
        const notices = response.notices;

        console.log('받은 공지:', notices);

        const $tbody = $table.querySelector('tbody');
        $tbody.innerHTML = ''; // 기존 내용 제거

        // 테이블 렌더링
        notices.forEach((notice) => {
            const row = `
                <tr data-index="${notice.index}">
                    <td>${notice.index}</td>
                    <td>${notice.title}</td>
                    <td>${notice.nickname}</td>
                    <td>${notice.createdAt.split('T')[0]}</td>
                    <td>${notice.view}</td>
                </tr>
            `;
            $tbody.insertAdjacentHTML('beforeend', row);
        });

        // 각 tr에 이벤트 등록
        const rows = $tbody.querySelectorAll('tr');

        rows.forEach((row) => {
            const index = row.dataset['index'];

            row.addEventListener('click', () => {
                // 조회수 증가 요청
                const xhr = new XMLHttpRequest();
                const formData = new FormData();
                formData.append('index', index);

                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) return;

                    if (xhr.status < 200 || xhr.status >= 300) {
                        dialog.showSimpleOk('경고', '요청중 오류');
                        return;
                    }

                    const result = JSON.parse(xhr.responseText);
                    if (result.result === 'success') {
                        // 상세 페이지로 이동
                        location.href = `${origin}/experts/notice/specific?index=${index}`;
                    } else {
                        dialog.showSimpleOk('조회수', '조회수 증가 실패');
                    }
                };

                xhr.open('POST', `/api/expert/notice`);
                xhr.send(formData);
            });
        });
    };

    xhr.open('GET', '/api/expert/notice');
    xhr.send();
};

window.addEventListener('DOMContentLoaded', () => {
    loadNotice();
});
