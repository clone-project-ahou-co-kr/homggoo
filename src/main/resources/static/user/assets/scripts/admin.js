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
        const $noticeTbody = document.querySelector('.notice .list-container > .list-table > tbody');
        const $articleTbody = document.querySelector('.board .list-container > .list-table > tbody');
        const $userTbody = document.querySelector('.user .list-container > .list-table > tbody');

        // 기존 내용 제거
        $noticeTbody.innerHTML = '';
        $articleTbody.innerHTML = '';
        $userTbody.innerHTML = '';

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
            $noticeTbody.insertAdjacentHTML('beforeend', rowHTML);
        });

        const renderNoticeChart = (notices) => {
            const titles = notices.map(n => n.title.length >5 ? n.title.slice(0, 5) + '...' : n.title);
            const views = notices.map(n => n.view || 0);
            const options = {
                chart: {
                    type: 'bar',
                    height: 350,
                }, plotOptions: {
                    bar: {
                        distributed: true, // 각 막대 독립 스타일
                        horizontal: true, // 수직 막대
                        columnWidth: '25%', // 막대 너비
                    }
                }, dataLabels: {
                    enabled: true,
                },
                xaxis: {
                    categories: titles,
                    title: {
                        text: '제목'
                    },
                    labels: {
                        rotate: 10 // 날짜 겹침 방지
                    }
                },
                yaxis: {
                    title: {
                        text: '조회수'
                    },
                    min: 0
                },
                title: {
                    text: '공지사항별 조회수',
                    align: 'center'
                }
            }
            const chartContainer = document.querySelector("#notice-chart");
            if (chartContainer) {
                chartContainer.innerHTML = ''; // 기존 차트 제거
                const chart = new ApexCharts(chartContainer, {
                    ...options,
                    series: [{
                        name: '조회수',
                        data: views
                    }]
                });
                chart.render();
            }
        }
        renderNoticeChart(notices);
        articles.forEach((article) => {
            const rowHTML = `
                <tr data-index="${article.id}">
                  <td class="category">${article.categoryDisplayText}</td>
                  <td class="title">${article.title}</td>
                  <td class="content">${article.content}</td>
                  <td class="created">${new Date(article.createdAt).toLocaleString()}</td>
                  <td class="nickname">${article.nickname}</td>
                  <td class="buttons">
                    <button class="edit">수정</button>
                    <button class="delete">삭제</button>
                  </td>
                </tr>
            `;
            $articleTbody.insertAdjacentHTML('beforeend', rowHTML);
        })
        // 게시글 날짜별 등록 수 차트 생성
        const renderArticleChart = (articles) => {
            const dateCounts = {};

            // 날짜별 게시글 수 집계
            articles.forEach((article) => {
                const date = new Date(article.createdAt).toISOString().split('T')[0];
                dateCounts[date] = (dateCounts[date] || 0) + 1;
            });

            const sortedDates = Object.keys(dateCounts).sort(); // 날짜 순 정렬
            const counts = sortedDates.map(date => dateCounts[date]);

            const options = {
                chart: {
                    type: 'bar',
                    height: 350,
                },
                plotOptions: {
                    bar: {
                        distributed: true, // 각 막대 독립 스타일
                        horizontal: true, // 수직 막대
                        columnWidth: '25%', // 막대 너비
                    }
                },
                dataLabels: {
                    //각 막대 위에 숫자(값)을 표시
                    enabled: true
                },
                xaxis: {
                    categories: sortedDates,
                    title: {
                        text: '날짜'
                    },
                    labels: {
                        rotate: 10 // 날짜 겹침 방지
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
                colors: undefined // 기본 색상 사용 (분산 색상 적용됨)
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
                <tr data-index="${user.index}">
                  <td class="email">${user.email}</td>
                  <td class="created">${new Date(user.createdAt).toLocaleString()}</td>
                  <td class="nickname">${user.nickname}</td>
                  <td class="buttons">
                    <button class="edit">승인</button>
                    <button class="delete">거절</button>
                  </td>
                </tr>
            `;
            $userTbody.insertAdjacentHTML('beforeend', rowHTML);
        })
        const renderUserChart = (users) => {
            let total = users.length;
            let deleted = users.filter(user => user.deleted).length;
            let active = total - deleted;

            const options = {
                chart: {
                    type: 'donut',
                    height: 350
                },
                labels: ['정상 회원', '탈퇴 회원'],
                series: [active, deleted],//series는 ApexCharts에서 실제 데이터를 차트에 넣는 핵심 속성이에요. -> 실제 시각화 할 데이터를 넣는곳.
                colors: ['#4CAF50', '#FF6B6B'],
                tooltip: {//툴팁이란, 사용자가 차트의 항목 위에 마우스를 올렸을 때 뜨는 말풍선입니다.
                    x: {
                        formatter: (val) => `${val}명`
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