document.addEventListener('DOMContentLoaded', () => {
    const $container = document.getElementById('container');
    const $category = document.getElementById('category');
    const $items = $category.querySelectorAll(':scope > .list > .item');
    const $mainContent = document.getElementById('main-content');

    const ininContentDiv = () => {
        $mainContent.querySelectorAll(':scope>.menu').forEach(menu => {
            menu.style.display = 'none';
        });
        const $all = $mainContent.querySelector(':scope>.all');
        if ($all) {
            $all.style.display = 'flex';
        }
        const $radioAll = document.getElementById('all');
        if ($radioAll) {
            $radioAll.checked = true;
        }

    }
    ininContentDiv();
    document.querySelectorAll('input[name="menu"]').forEach(radio => {
        radio.addEventListener('change', () => {
            $mainContent.querySelectorAll(':scope>.menu').forEach(menu => {
                menu.style.display = 'none';
            })
            $mainContent.querySelector(`:scope>.${radio.id}`).style.display = 'flex';
        })
    })
    window.addEventListener('pageshow', () => {
        ininContentDiv();
    })
});

const loadNotice = () => {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청중 오류');
            return;
        }

        const result = JSON.parse(xhr.responseText);
        if (result.result !== 'success') {
            dialog.showSimpleOk('경고', '공지사항을 불러오지 못했습니다.');
            return;
        }

        const notices = result.data;

        // 공지사항, 게시글 tbody 구분해서 선택
        const $noticeTbody = document.querySelector('.notice .list-container > .list-table > tbody');
        const $articleTbody = document.querySelector('.board .list-container > .list-table > tbody');
        const $userTbody = document.querySelector('.user .list-container > .list-table > tbody');

        // 기존 내용 제거
        $noticeTbody.innerHTML = '';
        $articleTbody.innerHTML = '';

        notices.forEach((notice) => {
            const rowHTML = `
                <tr data-index="${notice.index}">
                  <td class="title">${notice.title}</td>
                  <td class="content">${notice.content}</td>
                  <td class="created">${new Date(notice.createdAt).toLocaleString()}</td>
                  <td class="nickname">${notice.nickname || '관리자'}</td>
                  <td class="buttons">
                    <button class="edit">수정</button>
                    <button class="delete">삭제</button>
                  </td>
                </tr>
            `;

            if (notice.boardId === 'notice') {
                $noticeTbody.insertAdjacentHTML('beforeend', rowHTML);
            } else if (notice.boardId === 'board') {
                $articleTbody.insertAdjacentHTML('beforeend', rowHTML);
            }else if (notice.boardId === 'user') {
                $userTbody.insertAdjacentHTML('beforeend', rowHTML);
            }
        });

        // 이벤트 바인딩 (공지사항 + 게시글)
        document.querySelectorAll('.notice .list-table tbody tr, .article .list-table tbody tr')
            .forEach((row) => {
                row.addEventListener('click', () => {
                    const index = row.dataset['index'];
                    showNotice(index);
                });
            });
    };

    xhr.open('GET', '/api/user/notice');
    xhr.send();
};


const showNotice = (index) => {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청 중 오류');
            return;
        }

        const response = JSON.parse(xhr.responseText);
        const notice = response.payload;
        dialog.show({
            title: notice.title,
            content: notice.content,
            isContentHtml: true,
            buttons: [
                {
                    caption: "수정",
                    color: "blue",
                    onclick: (modalElement) => {
                        location.href = `${origin}/user/admin/modify?index=${notice.index}`;
                    }
                },
                {
                    caption: "닫기",
                    color: "gray",
                    onclick: ($modal) => {
                        dialog.hide($modal);
                    }
                }
            ]
        });

    };

    xhr.open('GET', `/api/user/admin/notice/view?index=${index}`);
    xhr.send();
};
loadNotice();