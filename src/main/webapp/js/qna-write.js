document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("qna-form");

    form.addEventListener("submit", function (event) {
        event.preventDefault();

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

        fetch("/qna/request", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버 응답이 올바르지 않습니다.");
                }
                return response.text();
            })
            .then(data => {
                alert("질문이 성공적으로 등록되었습니다!");
                window.location.href = "/qna"; // 질문 리스트 페이지로 이동
            })
            .catch(error => {
                console.error("Error:", error);
                alert("질문 등록 중 오류가 발생했습니다. 다시 시도해주세요.");
            });
    });
});
