// import fa from "../../../assets/libraries/ckeditor5/translations/fa";

const $findForm = document.getElementById('find-form');
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
        const users = result.user;
        const articles = result.articles;

        // 공지사항, 게시글 tbody 구분해서 선택
        const $noticeTbody = document.querySelector('.notice> .list-container > .list-table > tbody');
        const $articleTbody = document.querySelector('.article> .list-container > .list-table > tbody');
        const $userTbody = document.querySelector('.user> .list-container > .list-table > tbody');

        // 기존 내용 제거
        $noticeTbody.innerHTML = '';
        $articleTbody.innerHTML = '';
        $userTbody.innerHTML = '';

        notices.forEach((notice) => {
            const noticeContentWithHiddenFigure = notice.content.replace(
                /<figure([^>]*)>/g,
                '<figure$1 hidden style="display:none;">'
            ); //ckeditor는 figure안에 img를 감싸고 있어서 figure를 Hidden으로 준다.


            const hasImgTag = notice.content.includes('<img');
            const noticeContentImg = `<img src="/user/assets/images/picture.png" alt=""/>`;
            const rowHTML = `
                <tr data-index="${notice.index}">
                  <td class="title">${notice.title}</td>
                  <td class="content" style="display: flex; flex-direction: row; justify-content: flex-start; align-items: center;gap:0.5rem">${hasImgTag ? noticeContentImg + noticeContentWithHiddenFigure : notice.content}</td>
                  <td class="created">${notice.createdAt.split('T')[0]}</td>
                  <td  class="nickname">${notice.nickname || '관리자'}</td>
                  <td  class="view">${notice.view}</td>
                  <td  class="deleted" style="color: ${notice.deleted ? 'red' : 'inherit'};">${notice.deleted ? '삭제' : '정상'}</td>
                </tr>
            `;
            $noticeTbody.insertAdjacentHTML('beforeend', rowHTML);
        });
        const renderNoticeChart = (notices) => {
            const viewsByDate = {};
            notices.forEach((notice) => {
                const date = new Date(notice.createdAt);
                const formattedDate = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
                viewsByDate[formattedDate] = (viewsByDate[formattedDate] || 0) + (notice.view || 0);
            });

            const sortedDates = Object.keys(viewsByDate).sort();
            const views = sortedDates.map(date => viewsByDate[date]);

            const options = {
                chart: {type: 'bar', height: 200}, // width 생략하면 자동 맞춤
                plotOptions: {
                    bar: {
                        distributed: true,
                        horizontal: false,
                        columnWidth: '30%',
                    }
                },
                dataLabels: {enabled: true},
                xaxis: {
                    categories: sortedDates,
                    labels: {
                        rotate: -45,
                        style: {
                            fontSize: '0.75rem'
                        }
                    }
                },
                yaxis: {
                    title: {text: '조회수'},
                    min: 0
                },
                title: {
                    text: '공지사항별 조회수',
                    align: 'center'
                },
                legend: {show: false}
            };

            const chartContainer = document.querySelector("#notice-chart");
            if (chartContainer) {
                chartContainer.innerHTML = '';
                const chart = new ApexCharts(chartContainer, {
                    ...options,
                    series: [{name: '조회수', data: views}]
                });
                chart.render();
            }
        };
        renderNoticeChart(notices);

        articles.forEach((article) => {
            const rowHTML = `
                <tr data-index="${article.id}">
                  <td class="category">${article.categoryDisplayText}</td>
                  <td class="title">${article.title}</td>
                  <td class="content">${article.content}</td>
                  <td class="nickname">${article.nickname}</td>
                  <td class="created">${article.createdAt.split('T')[0]}</td>
                </tr>
            `;
            $articleTbody.insertAdjacentHTML('beforeend', rowHTML);
        })
        const articleRows = $articleTbody.querySelectorAll(':scope>tr');
        articleRows.forEach((article) => {
            article.addEventListener('click', () => {
                const $articleDialog = document.getElementById('articleDialog');
                const $articleModal = $articleDialog.querySelector(':scope>.---modal');
                const $articleContent = $articleModal.querySelector(':scope>.---content');

                $articleDialog.classList.add('-visible');
                $articleModal.classList.add('-visible');
                $articleModal.querySelector(':scope>.---title>.dialogCategory').innerHTML = article.querySelector(':scope>.category').textContent;
                $articleContent.querySelector(':scope > div >.dialogTitle').innerHTML = article.querySelector(':scope>.title').textContent;
                $articleContent.querySelector(':scope>div>.dialogContent').innerHTML =
                    article.querySelector(':scope>.content').textContent;
                $articleContent.querySelector(':scope>div>.dialogNickname').innerHTML = article.querySelector(':scope>.nickname').textContent;
                $articleContent.querySelector(':scope>div>.dialogCreatedAt').innerHTML = article.querySelector(':scope>.created').textContent;
                $articleContent.querySelector('.dialogCloseBtn').addEventListener('click', () => {
                    $articleDialog.classList.remove('-visible');
                    $articleModal.classList.remove('-visible');
                });
                $articleContent.querySelector(':scope>.button-container>.deleteBtn').addEventListener('click', () => {
                    $articleDialog.classList.remove('-visible');
                    $articleModal.classList.remove('-visible');
                    let index = article.dataset['index'];
                    const xhr = new XMLHttpRequest();
                    const formData = new FormData();
                    formData.append('index', index);
                    xhr.onreadystatechange = () => {
                        if (xhr.readyState !== XMLHttpRequest.DONE) {
                            return;
                        }
                        if (xhr.status < 200 || xhr.status >= 300) {
                            dialog.showSimpleOk('경고', '요청중 오류');
                            return;
                        }
                        const response = JSON.parse(xhr.responseText);
                        switch (response.result) {
                            case'success':
                                dialog.show({
                                    title: '삭제',
                                    content: '삭제하시는데 성공하였습니다.',
                                    buttons: [
                                        {
                                            caption: '확인',
                                            color: 'blue',
                                            onclick: ($modal) => {
                                                $modal.hide();
                                                document.body.querySelector(':scope>.--dialog').classList.remove('-visible');
                                            }
                                        }
                                    ]
                                })
                                loadNotice();
                                break;
                            case'failure':
                                dialog.showSimpleOk('삭제', '알 수 없는 이유로 삭제하지 못하였습니다. ')
                                break;
                            case'failure_session_expired':
                                dialog.showSimpleOk('삭제', '이미 삭제된 게시글입니다.');
                                break;
                            case'failure_absent':
                                dialog.showSimpleOk('삭제', '존재하지 않는 게시글입니다.');
                                break;
                            default:
                                break;
                        }
                    };
                    xhr.open('DELETE', '/api/user/article-delete');
                    xhr.send(formData);
                })
            })
        })
        // 게시글 날짜별 등록 수 차트 생성
        const renderArticleChart = (articles) => {
            const dateCounts = {};

            // 날짜별 게시글 수 집계 (YYYY.MM.DD 형식으로)
            articles.forEach((article) => {
                const date = new Date(article.createdAt);
                const formattedDate = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
                dateCounts[formattedDate] = (dateCounts[formattedDate] || 0) + 1;
            });

            const sortedDates = Object.keys(dateCounts).sort(); // 날짜 순 정렬
            const counts = sortedDates.map(date => dateCounts[date]);

            const options = {
                chart: {
                    type: 'bar',
                    height: 200,
                    toolbar: {show: false}
                },
                plotOptions: {
                    bar: {
                        distributed: true,
                        horizontal: false,
                        columnWidth: '20%',
                    }
                },
                dataLabels: {
                    enabled: true
                },
                xaxis: {
                    categories: sortedDates,
                    labels: {
                        rotate: -20,
                        style: {
                            fontSize: '0.75rem'
                        }
                    }
                },
                yaxis: {
                    title: {
                        text: '게시글 수'
                    },
                    min: 0
                },
                title: {
                    text: '날짜별 게시글 등록 수',
                    align: 'center'
                },
                legend: {show: false},
                colors: undefined
            };

            const chartContainer = document.querySelector("#article-chart");
            if (chartContainer) {
                chartContainer.innerHTML = '';
                const chart = new ApexCharts(chartContainer, {
                    ...options,
                    series: [{
                        name: '게시글 수',
                        data: counts
                    }]
                });
                chart.render();
            }
        };


        renderArticleChart(articles);
        users.forEach((user) => {
            const rowHTML = `
                <tr>
                  <td class="email">${user.email}</td>
                  <td class="nickname">${user.nickname}</td>
                  <td class="providerType">${user.providerType}</td>
                  <td class="created">${user.createdAt.split('T')[0]}</td>
                  <td class="deleted">${user.isDeleted ? '탈퇴됨' : '정상'}</td>
                </tr>
            `;
            $userTbody.insertAdjacentHTML('beforeend', rowHTML);
        })
        const rows = $userTbody.querySelectorAll('tr');
        rows.forEach((row) => {
            row.addEventListener('click', () => {
                const $userDialog = document.getElementById('userDialog');
                const $userModal = $userDialog.querySelector(':scope > .---modal');
                const $userContent = $userModal.querySelector(':scope > .---content');

                const nickname = row.querySelector('.nickname').textContent;

                $userContent.querySelector('.dialogNickname').innerHTML = nickname;
                $userContent.querySelector('.dialogEmail').innerHTML = row.querySelector('.email').textContent;
                $userContent.querySelector('.dialogCreatedAt').innerHTML = row.querySelector('.created').textContent;
                $userContent.querySelector('.dialogDeleted').innerHTML = row.querySelector('.deleted').textContent;

                const articleList = $userContent.querySelector(':scope>.dialogArticles>.articleLists');
                articleList.innerHTML = '';

                const userArticles = articles.filter((article) => article.nickname === nickname)
                if (userArticles.length === 0) {
                    articleList.innerHTML = `<li>작성된 게시글이 없습니다.</li>`;
                } else {
                    userArticles.forEach((article) => {
                        const li = document.createElement('li');
                        const a = document.createElement('a');
                        a.setAttribute('href', `${origin}/community/posts?id=${article.id}`);
                        a.textContent = `(${article.categoryDisplayText}) ${article.title}`;
                        li.appendChild(a);
                        articleList.appendChild(li);
                    })
                }

                // 다이얼로그 표시
                $userDialog.classList.add('-visible');
                $userModal.classList.add('-visible');

                // 닫기 버튼 이벤트 등록
                $userModal.querySelector('.dialogCloseBtn').addEventListener('click', () => {
                    $userDialog.classList.remove('-visible');
                    $userModal.classList.remove('-visible');
                });
            });

        })
        const renderUserChart = (users) => {
            let total = users.length;
            let deleted = users.filter(user => user.deleted).length;
            let active = total - deleted;

            const options = {
                chart: {
                    type: 'donut',
                    height: 200,
                    width: 300
                },
                labels: ['정상 회원', '탈퇴 회원'],
                series: [active, deleted],//series는 ApexCharts에서 실제 데이터를 차트에 넣는 핵심 속성이에요. -> 실제 시각화 할 데이터를 넣는곳.
                colors: ['#0478F8', '#727476'],
                tooltip: {//툴팁이란, 사용자가 차트의 항목 위에 마우스를 올렸을 때 뜨는 말풍선입니다.
                    x: {
                        formatter: (val) => `${val}명`,
                    }
                },
                title: {
                    text: '회원 상태 분포',
                    align: 'center'
                },
                plotOptions: {
                    pie: {
                        donut: {
                            labels: {
                                show: true,
                                total: {
                                    show: true,
                                    label: '전체 회원 수',
                                    formatter: () => `${total}명`
                                }
                            }
                        }
                    }
                }
            };

            const chartContainer = document.querySelector("#user-chart");
            if (chartContainer) {
                chartContainer.innerHTML = '';
                const chart = new ApexCharts(chartContainer, options);
                chart.render();
            }
        };

        renderUserChart(users);


        // 이벤트 바인딩 (공지사항)
        document.querySelectorAll('.notice>.list-container> .list-table> tbody> tr')
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
    const $noticeDialog = document.getElementById('noticeDialog');
    const $noticeModal = $noticeDialog.querySelector(':scope>.---modal');
    const $noticeContent = $noticeDialog.querySelector(':scope>.---modal>.---content');
    const restoreBtn = (index) => {
        const $restoreBtn = $noticeContent.querySelector(':scope>.button-container>.dialogRestoreBtn');

        // 기존 이벤트 제거 (중복 방지)
        const newBtn = $restoreBtn.cloneNode(true);
        $restoreBtn.parentNode.replaceChild(newBtn, $restoreBtn);

        newBtn.addEventListener('click', () => {
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('index', index);

            xhr.onreadystatechange = () => {
                if (xhr.readyState !== XMLHttpRequest.DONE) return;

                if (xhr.status < 200 || xhr.status >= 300) {
                    dialog.showSimpleOk('경고', '요청 중 오류');
                    return;
                }

                const response = JSON.parse(xhr.responseText);
                switch (response.result) {
                    case 'success':
                        dialog.show({
                            title: '공지사항',
                            content: '복구 완료 하였습니다.',
                            buttons: [
                                {
                                    caption: '확인',
                                    color: 'blue',
                                    onclick: ($modal) => {
                                        $modal.hide();
                                        $noticeDialog.setVisible(false);
                                        $noticeModal.setVisible(false);
                                        loadNotice();
                                    }
                                }
                            ]
                        });
                        break;
                    case 'failure':
                        dialog.showSimpleOk('공지사항', '게시글이 존재하지 않아 복구에 실패하였습니다.');
                        break;
                    default:
                        dialog.showSimpleOk('오류', '알 수 없는 오류가 발생했습니다.');
                }
            };

            xhr.open('PATCH', '/api/user/notice-restore');
            xhr.send(formData);
        });
    };

    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청 중 오류');
            return;
        }

        const response = JSON.parse(xhr.responseText);
        const notice = response.payload;
        $noticeContent.querySelector(':scope>div>.dialogTitle').innerHTML =
            notice.deleted ? `${notice.title} (삭제됨)` : notice.title;
        $noticeContent.querySelector(':scope>div>.dialogTitle').style.color = notice.deleted ? 'red' : 'black';

        $noticeContent.querySelector(':scope>div>.dialogContent').innerHTML = notice.content;
        $noticeContent.querySelector(':scope>div>.dialogCreatedAt').innerHTML = `${notice.createdAt.split('T')[0]} ${notice.createdAt.split('T')[1]}`;
        $noticeContent.querySelector(':scope>.button-container>.dialogRestoreBtn').style.display = 'none';
        $noticeContent.querySelector(':scope>.button-container>.dialogModifyBtn').style.display = 'none';

        if (notice.deleted) {
            $noticeContent.querySelector(':scope>.button-container>.dialogRestoreBtn').style.display = 'flex';
            restoreBtn(notice.index);
        } else {
            $noticeContent.querySelector(':scope>.button-container>.dialogModifyBtn').style.display = 'flex';
        }
        $noticeContent.querySelector(':scope>.button-container>.dialogModifyBtn').addEventListener('click', () => {
            location.href = `${origin}/user/admin/modify?index=${notice.index}`;
        })
        $noticeDialog.setVisible(true);
        $noticeModal.setVisible(true);
        $noticeContent.querySelector(':scope>.button-container>.dialogCloseBtn').addEventListener('click', () => {
            $noticeDialog.setVisible(false);
            $noticeModal.setVisible(false);
        })
        $noticeDialog.addEventListener('click', () => {
            $noticeDialog.setVisible(false);
            $noticeModal.setVisible(false);
        })
    };

    xhr.open('GET', `/api/user/admin/notice/view?index=${index}`);
    xhr.send();
};
loadNotice();


$findForm.onsubmit = (e) => {
    e.preventDefault();
    const by = $findForm['by'].value;
    const keyword = $findForm['keyword'].value;
    const params = new URLSearchParams();
    params.append('by', by);
    params.append('keyword', keyword);

    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) return;

        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('경고', '요청중 오류');
            return;
        }

        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'success':
                console.log('success');
                updateList('success', response.data);
                break;
            case 'failure':
                updateList('failure', undefined);
                break;
        }
    };

    xhr.open('GET', `/api/user/search?${params.toString()}`);
    xhr.send();
};

const updateList = (state, data) => {
    const $userSection = document.querySelector('#main-content .user.menu');
    const $userTbody = $userSection.querySelector('.list-container > table > tbody');
    $userTbody.innerHTML = '';

    if (state === 'success') {
        if (data.length === 0) {
            $userTbody.insertAdjacentHTML('beforeend', `
                <tr style="border-bottom:none"><td colspan="5"><h3>검색 결과가 없습니다.</h3></td></tr>
            `);
            return;
        }
        let rows = '';
        data.forEach(user => {
            rows += `
                <tr>
                    <td>${user.email}</td>
                    <td>${user.nickname}</td>
                    <td>${user.providerType}</td>
                    <td>${user.createdAt.split('T')[0]}</td>
                    <td>${user.deleted ? '탈퇴' : '정상'}</td>
                </tr>
            `;
        });
        $userTbody.insertAdjacentHTML('beforeend', rows);
    } else {
        $userTbody.insertAdjacentHTML('beforeend', `
            <tr><td colspan="5">검색 실패</td></tr>
        `);
    }
};
