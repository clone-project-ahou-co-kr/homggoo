document.addEventListener('DOMContentLoaded', () => {
    const $profile = document.getElementById('profile');
    const $menu = document.getElementById('menu');
    if ($profile === null || $menu === null) {
        return null;
    }

    $profile.addEventListener('click', (e) => {
        e.stopPropagation(); // 부모 전파 방지
        const isShown = $menu.style.display === 'block';
        $menu.style.display = isShown ? 'none' : 'block';
    });

    document.addEventListener('click', (e) => {
        if (!$menu.contains(e.target) && !$profile.contains(e.target)) {
            $menu.style.display = 'none';
        }
    });
});