document.addEventListener("DOMContentLoaded", function () {
    // FAQ/Q&A 버튼 클릭 시 활성화 상태 변경 및 페이지 이동
    document.querySelectorAll(".faq-tab").forEach(tab => {
        tab.addEventListener("click", function () {
            document.querySelectorAll(".faq-tab").forEach(t => t.classList.remove("active"));
            this.classList.add("active");

            // Q&A 버튼 클릭 시 /qna 페이지로 이동
            if (this.textContent.trim() === "Q&A") {
                window.location.href = "/qna";
            }
        });
    });

    // FAQ 질문 클릭 시 내용 펼치기/접기
    document.querySelectorAll(".faq-question").forEach(button => {
        button.addEventListener("click", function () {
            const answer = this.nextElementSibling;
            const icon = this.querySelector(".toggle-icon");

            if (answer.style.display === "block") {
                answer.style.display = "none";
                icon.textContent = "+";
            } else {
                answer.style.display = "block";
                icon.textContent = "-";
            }
        });
    });
});
