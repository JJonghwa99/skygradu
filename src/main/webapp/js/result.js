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
        const $this = $(this);
        let percent = parseFloat($this.attr("percent"));

        if (isNaN(percent) || !isFinite(percent)) {
            percent = 0;
        }

        // 0 ~ 1 범위 제한
        percent = Math.max(0, Math.min(1, percent));

        const targetDegree = Math.round(percent * 360);
        const targetPercent = Math.round(percent * 100);
        const pieColor = "#0066FF";
        const duration = 1500;
        const fps = 60;
        const totalFrames = Math.round((duration / 1000) * fps);
        let currentFrame = 0;

        const $pieChart = $this.closest(".pie-chart-color");

        const interval = setInterval(() => {
            currentFrame++;
            const progress = currentFrame / totalFrames;

            const currentPercent = Math.round(progress * targetPercent);
            const currentDegree = Math.round(progress * targetDegree);


            $this.text(currentPercent + "%");

            $pieChart.css({
                background: `conic-gradient(${pieColor} ${currentDegree}deg, #e4e4e4 ${currentDegree}deg)`
            });

            if (currentFrame >= totalFrames) {
                clearInterval(interval);
            }
        }, 1000 / fps);
    });


    $(".recommend").on("click", function () {
        const target = $(this).attr("data-target");
        $(`#${target}`).fadeIn();
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