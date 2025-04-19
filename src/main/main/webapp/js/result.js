$(document).ready(function () {
    $(".count").each(function () {
        const $this = $(this);
        const countTo = parseFloat($this.text());

        if (!isNaN(countTo)) {
            $({ countNum: 0 }).animate(
                { countNum: countTo },
                {
                    duration: 1500,
                    easing: "swing",
                    step: function () {
                        $this.text(Math.floor(this.countNum));
                    },
                    complete: function () {
                        $this.text(Math.floor(this.countNum));
                    },
                }
            );
        } else {
            $this.text("0");
        }
    });

    $(".percent").each(function () {
        let percent = parseFloat($(this).attr("percent"));

        if (isNaN(percent) || !isFinite(percent)) {
            percent = 0;
        }

        percent = Math.max(0, Math.min(1, percent));

        const degree = Math.round(percent * 360);
        const percentText = Math.round(percent * 100) + "%";

        $(this).text(percentText);

        const pieColor = "#0066FF";
        $(this).closest(".pie-chart-color").css({
            background: `conic-gradient(${pieColor} ${degree}deg, #e4e4e4 ${degree}deg)`
        });
    });

    $(".recommend").on("click", function () {
        const target = $(this).attr("data-target");

        // 교양선택 버튼의 경우 recommend.html 로 이동하도록 처리
        if (target === "modal-culture-e") {
            // 필요한 파라미터나 상태가 있다면 URL 파라미터로 추가할 수 있음
            window.location.href = "/recommend.html";
        } else {
            // 나머지 버튼은 기존 모달 열기 처리
            $(`#${target}`).fadeIn();
        }
    });

    $(".modal .close").on("click", function () {
        $(this).closest(".modal").fadeOut();
    });

    $(".modal").on("click", function (e) {
        if ($(e.target).hasClass("modal")) {
            $(this).fadeOut();
        }
    });
});

function modal_close(e) {
    const target = e.getAttribute("close");
    $(`#${target}`).fadeOut();
}