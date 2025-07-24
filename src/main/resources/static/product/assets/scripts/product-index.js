const track = document.querySelector(".carousel-track");
const items = document.querySelectorAll(".carousel-item");
const prevBtn = document.querySelector(".carousel-control.prev");
const nextBtn = document.querySelector(".carousel-control.next");

let carouselIndex = 0;
const totalItems = items.length;
const visibleItems = 3;

const updateCarousel = () => {
    track.style.transform = `translateX(-${carouselIndex * (100 / visibleItems)}%)`;
};

const moveNext = () => {
    carouselIndex++;
    if (carouselIndex > totalItems - visibleItems) {
        carouselIndex = 0; // 순환 슬라이드로 처음으로
    }
    updateCarousel();
};

const movePrev = () => {
    carouselIndex--;
    if (carouselIndex < 0) {
        carouselIndex = totalItems - visibleItems; // 끝으로 순환
    }
    updateCarousel();
};

// 버튼 클릭 이벤트
nextBtn.addEventListener("click", () => {
    moveNext();
    resetAutoSlide();
});

prevBtn.addEventListener("click", () => {
    movePrev();
    resetAutoSlide();
});

// 3초마다 자동 슬라이드
let autoSlide = setInterval(moveNext, 3000);

// 버튼 클릭 시 자동 슬라이드 리셋
const resetAutoSlide = () => {
    clearInterval(autoSlide);
    autoSlide = setInterval(moveNext, 3000);
};