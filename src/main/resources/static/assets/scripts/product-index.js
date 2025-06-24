let curPos = 0; // 현재 보여주는 이미지의 인덱스 번호
let positionValue = 0; // 이미지 태그의 위치 값 지정할 변수
const IMAGE_WIDTH = 1455;

const prevBtn = document.querySelector(".prev");
const nextBtn = document.querySelector(".next");
const images = document.querySelector(".images");

// 다음 이미지로 이동하는 함수
function next(){
    // 현재 이미지가 마지막 이미지가 아닌 경우에만 이동 가능
    if(curPos < 3){
        prevBtn.removeAttribute('disabled'); // 이전 버튼 활성화
        positionValue -= IMAGE_WIDTH; // 다음 이미지 위치로 이동
        images.style.transform = `translateX(${positionValue}px)`; // 이미지 이동 애니메이션
        curPos += 1; // 현재 이미지 인덱스 증가
    }
    if(curPos === 3){
        nextBtn.setAttribute('disabled','true');
    }
}

// 이전 이미지로 이동하는 함수
function prev(){
    if(curPos > 0){
        nextBtn.removeAttribute('disabled'); // 다음 버튼 활성화
        positionValue += IMAGE_WIDTH; // 이전 이미지 위치로 이동
        images.style.transform = `translateX(${positionValue}px)`; // 이미지 이동 애니메이션
        curPos -= 1; // 현재 이미지 인덱스 감소
    }
    if(curPos === 0){
        prevBtn.setAttribute('disabled','true');
    }
}


function init(){
    prevBtn.setAttribute('disabled','true'); // 페이지 로드 시 이전 버튼 비활성화
    prevBtn.addEventListener("click", prev); // 이전 버튼에 클릭 이벤트 추가
    nextBtn.addEventListener("click", next); // 다음 버튼에 클릭 이벤트 추가
}

init(); // 초기화 함수 호출