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

nextBtn.addEventListener("click", () => {
    carouselIndex++;
    if (carouselIndex > totalItems - visibleItems) {
        carouselIndex = 0; // 순환 슬라이드로 처음으로
    }
    updateCarousel();
});

prevBtn.addEventListener("click", () => {
    carouselIndex--;
    if (carouselIndex < 0) {
        carouselIndex = totalItems - visibleItems; // 끝으로 순환
    }
    updateCarousel();
});