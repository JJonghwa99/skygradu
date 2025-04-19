document.addEventListener("DOMContentLoaded", () => {
    const cultureElectiveCourses = [
        { name: "글쓰기와 표현", code: "111111", credit: 2, completed: false },
        { name: "심리학 개론", code: "222222", credit: 3, completed: false },
        { name: "디지털 문해력", code: "333333", credit: 1, completed: true },
        { name: "환경과 미래", code: "444444", credit: 3, completed: false },
        { name: "인간관계론", code: "555555", credit: 2, completed: true }
    ];

    const container = document.getElementById("recommend-culture-elective");
    const filterButtons = document.querySelectorAll(".filter-btn");

    renderCards(sortByCompletion(cultureElectiveCourses));

    filterButtons.forEach(button => {
        button.addEventListener("click", () => {
            const selectedCredit = parseInt(button.getAttribute("data-credit"));

            filterButtons.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");

            const filtered = isNaN(selectedCredit)
                ? cultureElectiveCourses
                : cultureElectiveCourses.filter(course => course.credit === selectedCredit);

            renderCards(sortByCompletion(filtered));
        });
    });

    // 렌더링 함수
    function renderCards(courseList) {
        container.innerHTML = "";

        courseList.forEach(course => {
            const card = document.createElement("div");
            card.className = "recommend-card";

            card.innerHTML = `
                <h5 class="recommend-subject">${course.name}</h5>
                <p class="recommend-info">학수번호: ${course.code} | ${course.credit}학점</p>
                <span class="badge ${course.completed ? "bg-success" : "bg-danger"}">
                    ${course.completed ? "이수" : "미이수"}
                </span>
            `;

            container.appendChild(card);
        });
    }

    // 미이수 우선 정렬
    function sortByCompletion(courseList) {
        return [...courseList].sort((a, b) => Number(a.completed) - Number(b.completed));
    }
});
