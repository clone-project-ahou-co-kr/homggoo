document.addEventListener('DOMContentLoaded', () => {
    const $profile = document.getElementById('profile');
    const $menu = document.getElementById('menu');
    const miniHeader = document.getElementById('mini-header');
    const categoryNav = miniHeader.querySelector('.category');

    const shoppingLink = document.querySelector('.shopping');
    const noticeLink = document.querySelector('.notice');
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

    // const noticeCategoryHTML = `
    //   <a href="/experts">공지사항</a>
    //   <a href="/experts/event">이벤트</a>
    // `;

    const setCategoryHTML = (html) => {
        categoryNav.innerHTML = html;
        setting(); // active 클래스 다시 적용
    };

    const showMiniHeader = () => {
        miniHeader.classList.add('hovered');
    };

    const hideMiniHeader = () => {
        miniHeader.classList.remove('hovered');
    };

    let hoverTimeout = null;
    let currentSection = '';

    const onMouseEnter = () => {
        clearTimeout(hoverTimeout);
        showMiniHeader();

        // 현재 섹션 상태에 따라 카테고리 표시
        switch (currentSection) {
            case 'shopping':
                setCategoryHTML(shoppingCategoryHTML);
                break;
            // case 'notice':
            //     setCategoryHTML(noticeCategoryHTML);
            //     break;
            case 'community':
            default:
                setCategoryHTML(defaultCategoryHTML);
                break;
        }
    };

    const onMouseLeave = () => {
        hoverTimeout = setTimeout(() => {
            if (!header.matches(':hover') && !miniHeader.matches(':hover')) {
                currentSection = '';
                hideMiniHeader();
                if (currentPath.startsWith('/product')) {
                    setCategoryHTML(shoppingCategoryHTML);
                }  else if (currentPath.startsWith('/community')) {
                    setCategoryHTML(defaultCategoryHTML);
                }
            }
        }, 100);
    };

    // 각 메뉴 hover 시 섹션 상태와 카테고리 설정
    shoppingLink?.addEventListener('mouseenter', () => {
        currentSection = 'shopping';
        setCategoryHTML(shoppingCategoryHTML);
    });

    noticeLink?.addEventListener('mouseenter', () => {
        currentSection = 'notice';
        setCategoryHTML(noticeCategoryHTML);
    });

    communityLink?.addEventListener('mouseenter', () => {
        currentSection = 'community';
        setCategoryHTML(defaultCategoryHTML);
    });

    // header & miniHeader hover 감지
    header.addEventListener('mouseenter', onMouseEnter);
    header.addEventListener('mouseleave', onMouseLeave);
    miniHeader.addEventListener('mouseenter', onMouseEnter);
    miniHeader.addEventListener('mouseleave', onMouseLeave);

    // 프로필 메뉴 토글
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

    // 현재 경로에 따라 active 클래스 설정
    const currentPath = window.location.pathname;
    const setting = () => {
        document.querySelectorAll(".category a").forEach(link => {
            if (link.getAttribute("href") === currentPath) {
                link.classList.add("active");
            } else {
                link.classList.remove("active");
            }
        });
    };

    if (currentPath.startsWith('/product')) {
        currentSection = 'shopping';
        setCategoryHTML(shoppingCategoryHTML);
    } else if (currentPath.startsWith('/experts')) {
        currentSection = 'notice';
        setCategoryHTML(noticeCategoryHTML);
    } else {
        currentSection = 'community';
        setCategoryHTML(defaultCategoryHTML);
    }

    setting();
});
