const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn")
const $interiorContainer = document.getElementById('interior-container');


const $item = $interiorContainer.querySelectorAll(':scope>.common-size>.list>.item');
const visibleCards = 4.5; // 한 화면에 보이는 수
const scrollCards = 4;    // 한 번에 넘길 수
let startIndex = 0;