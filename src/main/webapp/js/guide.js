document.addEventListener("DOMContentLoaded", function () {
    console.log("이용안내 페이지 로드 완료");

    // 이용안내 / 개인정보처리방침 선택 탭 변경
    document.querySelectorAll(".guide-tab").forEach(tab => {
        tab.addEventListener("click", function () {
            document.querySelectorAll(".guide-tab").forEach(t => t.classList.remove("active"));
            this.classList.add("active");
        });
    });
});
