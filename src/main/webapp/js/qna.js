document.addEventListener("DOMContentLoaded", function () {
    // FAQ / Q&A 버튼 클릭 이벤트
    document.querySelectorAll(".faq-tab").forEach(tab => {
        tab.addEventListener("click", function () {
            if (this.textContent.trim() === "FAQs") {
                window.location.href = "/faq";
            } else if (this.textContent.trim() === "Q&A") {
                window.location.href = "/qna";
            }
        });
    });

    // 질문 등록 버튼 클릭 시 이동
    document.getElementById("qna-submit-btn").addEventListener("click", function () {
        window.location.href = "/qna/write";
    });
});
