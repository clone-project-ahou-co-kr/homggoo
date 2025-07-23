document.addEventListener('DOMContentLoaded', () => {
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
});
