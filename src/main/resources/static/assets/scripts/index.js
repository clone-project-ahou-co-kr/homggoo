document.addEventListener('DOMContentLoaded', () => {
    const $header = document.getElementById('header');
    const $profile = document.getElementById('profile');
    const $menu = document.getElementById('menu');
    const miniHeader = document.getElementById('mini-header');
    const categoryNav = miniHeader.querySelector('.category');

    const shoppingLink = document.querySelector('.shopping');
    const noticeLink = document.querySelector('.nav-menu.notice');
    const communityLink = document.querySelector('.community');
    const header = document.getElementById('header');

    const defaultCategoryHTML = `
      <a href="/"><span>홈</span></a>
      <a href="/community/interior">집꾸미기</a>
      <a href="/community/product">꿀템발견</a>
    `;

    const shoppingCategoryHTML = `
      <a href="/product/">쇼핑홈</a>
      <a href="/product/all">카테고리</a>
    `;

    const noticeCategoryHTML = `
      <a href="/experts/">홈</a>
    `;

    const currentPath = window.location.pathname;
    const getHTMLByPath = () => {
        if (currentPath.startsWith('/product')) return shoppingCategoryHTML;
        if (currentPath.startsWith('/experts')) return noticeCategoryHTML;
        return defaultCategoryHTML;
    };

    const setCategoryHTML = (html) => {
        categoryNav.innerHTML = html;
        setTimeout(() => {
            setting();
        }, 0);
    };

    const setting = () => {
        document.querySelectorAll(".category a").forEach(link => {
            if (link.getAttribute("href") === currentPath) {
                link.classList.add("active");
            } else {
                link.classList.remove("active");
            }
        });
    };

    // 초기 설정
    setCategoryHTML(getHTMLByPath());


    shoppingLink?.addEventListener('mouseenter', () => setCategoryHTML(shoppingCategoryHTML));
    noticeLink?.addEventListener('mouseenter', () => setCategoryHTML(noticeCategoryHTML));
    communityLink?.addEventListener('mouseenter', () => setCategoryHTML(defaultCategoryHTML));

    // mouseleave 시 원래 경로 기준 복구
    [header, miniHeader].forEach(el => {
        el.addEventListener('mouseleave', () => {
            setTimeout(() => {
                if (!header.matches(':hover') && !miniHeader.matches(':hover')) {
                    setCategoryHTML(getHTMLByPath());
                }
            }, 100);
        });
    });

    if ($profile && $menu) {
        $profile.addEventListener('click', (e) => {
            e.stopPropagation();
            $menu.style.display = ($menu.style.display === 'block') ? 'none' : 'block';
        });

        document.addEventListener('click', (e) => {
            if (!$menu.contains(e.target) && !$profile.contains(e.target)) {
                $menu.style.display = 'none';
            }
        });
    }

    const sessionTimeout = () => {

        const xhr = new XMLHttpRequest();

        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.status < 200 || xhr.status >= 300) {
                dialog.showSimpleOk('오류', '요청을 처리하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.', {
                    onClickCallback: () => location.reload()
                });
                return;
            }

            const response = JSON.parse(xhr.responseText);
            const $time = document.getElementById('remainTime');
            if ($time) {
                let remaining = response.remaining; // 단위: 초

                $time.innerText = formatTime(remaining);

                // 1초마다 업데이트
                const timer = setInterval(() => {
                    remaining--;
                    if (remaining <= 0) {
                        clearInterval(timer);
                        $time.innerText = "세션 만료됨";
                        return;
                    }
                    $time.innerText = formatTime(remaining);
                }, 1000);
            }

        }
        xhr.open('GET','/api/session-info/');
        xhr.send();
    }


    const formatTime = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const sec = seconds % 60;
        return `${minutes}분 ${sec < 10 ? '0' + sec : sec}초`;
    }

    sessionTimeout();
});
