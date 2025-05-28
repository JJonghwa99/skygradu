document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("recommend-culture-elective");
    const filterButtons = document.querySelectorAll(".filter-btn");

    // 최초 로드 시 서버에서 주입된 배열을 정렬·렌더링
    renderCards(sortByCompletion(cultureElectiveCourses));

    filterButtons.forEach(button => {
        button.addEventListener("click", () => {
            const selectedCredit = button.getAttribute("data-credit");

            // 활성 버튼 토글
            filterButtons.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");

            // "all" 이면 전체, 숫자면 credits 프로퍼티로 필터
            const filtered = selectedCredit === "all"
                ? cultureElectiveCourses
                : cultureElectiveCourses.filter(course =>
                    String(course.credits) === selectedCredit
                );

            renderCards(sortByCompletion(filtered));
        });
    });

    // 카드 렌더링 함수
    function renderCards(courseList) {
        container.innerHTML = "";
        courseList.forEach(course => {
            const card = document.createElement("div");
            card.className = "recommend-card";
            card.innerHTML = `
                <h5 class="recommend-subject">${course.courseName}</h5>
                <p class="recommend-info">
                    과목코드: ${course.lectureCode} | ${course.credits}학점
                </p>
                <span class="badge bg-${course.completed ? "success" : "danger"}">
                    ${course.completed ? "이수" : "미이수"}
                </span>
            `;
            container.appendChild(card);
        });
    }

    // completed 필드(false 우선)가 아직 모두 false 이면 원본 순서(count 내림차순)가 유지됩니다
    function sortByCompletion(arr) {
        return [...arr].sort((a, b) => Number(a.completed) - Number(b.completed));
    }
});
