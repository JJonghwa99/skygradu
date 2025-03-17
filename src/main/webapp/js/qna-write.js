document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("qna-form");

    form.addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        const title = document.getElementById("title").value.trim();
        const content = document.getElementById("content").value.trim();
        const anonymous = document.getElementById("anonymous").checked;

        if (!title || !content) {
            alert("제목과 내용을 입력하세요.");
            return;
        }

        const requestData = {
            title: title,
            content: content,
            anonymous: anonymous
        };

        fetch("/api/qna/write", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        })
        .then(response => response.text())
        .then(data => {
            alert(data);
            window.location.href = "/qna"; // 질문 리스트 페이지로 이동
        })
        .catch(error => console.error("Error:", error));
    });
});
