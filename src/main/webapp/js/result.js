$(document).ready(function () {
    $(".count").each(function () {
        const $this = $(this);
        const countTo = parseFloat($this.text());

        if (!isNaN(countTo)) {
            $({countNum: 0}).animate(
                {countNum: countTo},
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
        const fps = 120;
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

        // 교양선택 버튼의 경우 recommend.html 로 이동하도록 처리
        if (target === "modal-culture-e") {
            // 필요한 파라미터나 상태가 있다면 URL 파라미터로 추가할 수 있음
            window.location.href = "/recommend";
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

$(function () {
    const userMajor = window.userMajor;
    const completedNames = window.completedNames;

    const recommendations = {
        '신학과': {
            "modal-major-i": [],  // 전필 과목 추가
            "modal-major-s": [
                {"code": "20413", "name": "예수와하나님나라", "credits": 3},
                {"code": "21176", "name": "문화콘텐츠:창작과비평", "credits": 3},
                {"code": "00158", "name": "한국교회사", "credits": 3},
                {"code": "20438", "name": "레포츠와선교", "credits": 3},
                {"code": "21475", "name": "신약성서이야기", "credits": 3},
                {"code": "21493", "name": "청소년상담실습", "credits": 3},
                {"code": "17814", "name": "이단운동의역사", "credits": 3},
                {"code": "17815", "name": "사회봉사신학", "credits": 3},
                {"code": "20415", "name": "왕과예언", "credits": 3},
                {"code": "20416", "name": "바울과복음", "credits": 3},
                {"code": "20417", "name": "현대영성신학", "credits": 3},
                {"code": "21639", "name": "학원복음화인큐베이팅", "credits": 3},
                {"code": "00471", "name": "예배와설교", "credits": 3},
                {"code": "04531", "name": "사중복음", "credits": 3},
                {"code": "20419", "name": "성서히브리어", "credits": 3}
            ]
        },
        '신학부': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "20413", "name": "예수와하나님나라", "credits": 3},
                {"code": "21176", "name": "문화콘텐츠:창작과비평", "credits": 3},
                {"code": "00158", "name": "한국교회사", "credits": 3},
                {"code": "20438", "name": "레포츠와선교", "credits": 3},
                {"code": "21475", "name": "신약성서이야기", "credits": 3},
                {"code": "21493", "name": "청소년상담실습", "credits": 3},
                {"code": "17814", "name": "이단운동의역사", "credits": 3},
                {"code": "17815", "name": "사회봉사신학", "credits": 3},
                {"code": "20415", "name": "왕과예언", "credits": 3},
                {"code": "20416", "name": "바울과복음", "credits": 3},
                {"code": "20417", "name": "현대영성신학", "credits": 3},
                {"code": "21639", "name": "학원복음화인큐베이팅", "credits": 3},
                {"code": "00471", "name": "예배와설교", "credits": 3},
                {"code": "04531", "name": "사중복음", "credits": 3},
                {"code": "20419", "name": "성서히브리어", "credits": 3}
            ]
        },
        '기독교교육상담과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "19466", "name": "상담의이론과실제", "credits": 3},
                {"code": "20959", "name": "청소년문화", "credits": 3},
                {"code": "21455", "name": "기독교교육과미디어", "credits": 3},
                {"code": "18350", "name": "기독교교육지도자론", "credits": 3},
                {"code": "20422", "name": "성서교수법", "credits": 3},
                {"code": "20424", "name": "어린이•청소년사역", "credits": 3},
                {"code": "20965", "name": "기독교교육과예술치료", "credits": 3},
                {"code": "21148", "name": "청소년문제와보호", "credits": 3},
                {"code": "00166", "name": "종교심리학", "credits": 3},
                {"code": "00511", "name": "한국교회역사개론", "credits": 3},
                {"code": "18208", "name": "종교교과교육론", "credits": 3},
                {"code": "20966", "name": "청소년심리및상담", "credits": 3},
                {"code": "21639", "name": "학원복음화인큐베이팅", "credits": 3},
                {"code": "00412", "name": "조직신학개론", "credits": 3},
                {"code": "00520", "name": "기독교교육행정", "credits": 3},
                {"code": "20968", "name": "청소년활동", "credits": 3}
            ]
        },
        '문화선교학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "20438", "name": "레포츠와선교", "credits": 3},
                {"code": "20955", "name": "타문화탐험과대화", "credits": 3},
                {"code": "21176", "name": "문화콘텐츠:창작과비평", "credits": 3},
                {"code": "21484", "name": "다문화가족복지론(원격수업)", "credits": 3},
                {"code": "21475", "name": "신약성서이야기", "credits": 3},
                {"code": "21478", "name": "사회적기업:창업과경영", "credits": 3},
                {"code": "21480", "name": "현대음악과CCM", "credits": 3},
                {"code": "04531", "name": "사중복음", "credits": 3},
                {"code": "20948", "name": "아시아문화와선교", "credits": 3},
                {"code": "20969", "name": "기독교영화제작", "credits": 3},
                {"code": "21639", "name": "학원복음화인큐베이팅", "credits": 3},
                {"code": "20440", "name": "문화와선교:이슈와사례", "credits": 3}
            ]
        },
        '국어국문학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "01945", "name": "세계문학의흐름", "credits": 3},
                {"code": "17826", "name": "문화콘텐츠론", "credits": 3},
                {"code": "19580", "name": "고전문학의세계", "credits": 3},
                {"code": "21518", "name": "문장작법이해", "credits": 3},
                {"code": "01935", "name": "구비문학의이해", "credits": 3},
                {"code": "21526", "name": "희곡창작실기론", "credits": 3},
                {"code": "21531", "name": "한국어전산학심화", "credits": 3},
                {"code": "21539", "name": "현대시론과현대시인론", "credits": 3},
                {"code": "01931", "name": "현대시인론", "credits": 3},
                {"code": "01937", "name": "고전시가론", "credits": 3},
                {"code": "01938", "name": "소설창작실기론", "credits": 3},
                {"code": "18346", "name": "국어학강독", "credits": 3},
                {"code": "01934", "name": "현대문학연습", "credits": 3},
                {"code": "01961", "name": "국어학특강", "credits": 3},
                {"code": "20443", "name": "한국어담화와의미", "credits": 3}
            ]
        },
        '영어영문학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "01006", "name": "영어회화(2)", "credits": 3},
                {"code": "20911", "name": "영어문장구조와어휘", "credits": 3},
                {"code": "01049", "name": "영미아동문학", "credits": 3},
                {"code": "01092", "name": "영어글쓰기(2)", "credits": 3},
                {"code": "01094", "name": "미국문학개론", "credits": 3},
                {"code": "07429", "name": "영어어휘연구", "credits": 3},
                {"code": "20912", "name": "IT와영어영문학", "credits": 3},
                {"code": "01043", "name": "미국소설", "credits": 3},
                {"code": "01060", "name": "영어교육론", "credits": 3},
                {"code": "01080", "name": "미국시", "credits": 3},
                {"code": "02404", "name": "영어자유토론", "credits": 3},
                {"code": "18347", "name": "BusinessEnglish", "credits": 3},
                {"code": "18323", "name": "영어프리젠테이션", "credits": 3},
                {"code": "19801", "name": "국제무역영어", "credits": 3}
            ]
        },
        '중어중문학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "19586", "name": "중국문화와도시공간", "credits": 3},
                {"code": "21141", "name": "현대중국의이해", "credits": 3},
                {"code": "21690", "name": "기초중국어회화(심화)", "credits": 3},
                {"code": "02151", "name": "중국어학입문", "credits": 3},
                {"code": "02153", "name": "중급중국어강독", "credits": 3},
                {"code": "06318", "name": "중국고전사상의이해", "credits": 3},
                {"code": "18212", "name": "중국어교과교재연구및지도법", "credits": 3},
                {"code": "19493", "name": "고급중국어회화", "credits": 3},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "21411", "name": "진로탐색(2)", "credits": 3},
                {"code": "02117", "name": "중국어작문", "credits": 3},
                {"code": "07507", "name": "중국현당대작품감상", "credits": 3},
                {"code": "19588", "name": "비즈니스중국어(2)", "credits": 3},
                {"code": "19812", "name": "중국고전소설과문화", "credits": 3},
                {"code": "02156", "name": "중국어번역연습", "credits": 3},
                {"code": "07455", "name": "시사중국어", "credits": 3}
            ]
        },
        '국제개발협력학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "00703", "name": "경제학개론", "credits": 3},
                {"code": "00742", "name": "도시학개론", "credits": 3},
                {"code": "18463", "name": "공간구조론", "credits": 3},
                {"code": "21447", "name": "지역사회개발프로젝트", "credits": 3},
                {"code": "00701", "name": "사회학개론", "credits": 3},
                {"code": "19894", "name": "국제개발협력기구론", "credits": 3},
                {"code": "19895", "name": "지역조사방법론", "credits": 3},
                {"code": "19896", "name": "인구학", "credits": 3},
                {"code": "19899", "name": "국제관계론", "credits": 3},
                {"code": "19900", "name": "국제개발협력의동향", "credits": 3},
                {"code": "19902", "name": "지역학개론", "credits": 3},
                {"code": "04506", "name": "국토및지역정책론", "credits": 3},
                {"code": "19907", "name": "국제개발협력평가", "credits": 3},
                {"code": "19909", "name": "인구이동및국제교류", "credits": 3}
            ]
        },
        '사회복지학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "00825", "name": "인간행동과사회환경", "credits": 3},
                {"code": "20738", "name": "사회복지역사", "credits": 3},
                {"code": "00844", "name": "지역사회복지론", "credits": 3},
                {"code": "00853", "name": "노인복지론", "credits": 3},
                {"code": "00875", "name": "가족복지론", "credits": 3},
                {"code": "00878", "name": "사회복지실천기술론", "credits": 3},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "00807", "name": "사회복지행정론", "credits": 3},
                {"code": "00851", "name": "교정복지론", "credits": 3},
                {"code": "00874", "name": "청소년복지론", "credits": 3},
                {"code": "00881", "name": "사회복지현장실습(1)", "credits": 3},
                {"code": "00887", "name": "사회복지자료분석론", "credits": 3},
                {"code": "20735", "name": "사회복지법제와실천", "credits": 3},
                {"code": "21053", "name": "빈곤론", "credits": 3},
                {"code": "00893", "name": "복지정보화론", "credits": 3},
                {"code": "06301", "name": "사회복지지도감독론", "credits": 3},
                {"code": "17995", "name": "사회복지특강(2)", "credits": 3}
            ]
        },
        '행정학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "01212", "name": "헌법", "credits": 3},
                {"code": "18533", "name": "사회과학방법론", "credits": 3},
                {"code": "01266", "name": "행정관리론", "credits": 3},
                {"code": "20453", "name": "형사법", "credits": 3},
                {"code": "01209", "name": "한국정부론", "credits": 3},
                {"code": "01250", "name": "공기업론", "credits": 3},
                {"code": "01254", "name": "도시행정론", "credits": 3},
                {"code": "17851", "name": "정책분석론", "credits": 3},
                {"code": "01230", "name": "정책사례연구", "credits": 3},
                {"code": "01269", "name": "행정학연습", "credits": 3},
                {"code": "06222", "name": "지방재정론", "credits": 3},
                {"code": "20457", "name": "현대행정과NGO", "credits": 3}
            ]
        },
        '행정학부': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "01212", "name": "헌법", "credits": 3},
                {"code": "18533", "name": "사회과학방법론", "credits": 3},
                {"code": "01266", "name": "행정관리론", "credits": 3},
                {"code": "20453", "name": "형사법", "credits": 3},
                {"code": "01209", "name": "한국정부론", "credits": 3},
                {"code": "01250", "name": "공기업론", "credits": 3},
                {"code": "01254", "name": "도시행정론", "credits": 3},
                {"code": "17851", "name": "정책분석론", "credits": 3},
                {"code": "01230", "name": "정책사례연구", "credits": 3},
                {"code": "01269", "name": "행정학연습", "credits": 3},
                {"code": "06222", "name": "지방재정론", "credits": 3},
                {"code": "20457", "name": "현대행정과NGO", "credits": 3}]
        },
        '관광학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "21667", "name": "관광영어", "credits": 3},
                {"code": "21668", "name": "환대산업론", "credits": 3},
                {"code": "19914", "name": "여가문화론", "credits": 3},
                {"code": "21339", "name": "글로벌문화탐방", "credits": 3},
                {"code": "21343", "name": "문화관광콘텐츠론", "credits": 3},
                {"code": "21344", "name": "호스피탈리티서비스론", "credits": 3},
                {"code": "21346", "name": "문화관광마케팅", "credits": 3},
                {"code": "21349", "name": "관광소셜미디어와빅데이터분석", "credits": 3},
                {"code": "19922", "name": "관광상담론", "credits": 3},
                {"code": "19923", "name": "관광조사분석", "credits": 3},
                {"code": "19935", "name": "이벤트사업론", "credits": 3},
                {"code": "20634", "name": "관광법규", "credits": 3},
                {"code": "20635", "name": "관광정보처리실무", "credits": 3},
                {"code": "19943", "name": "호텔현장실습", "credits": 3},
                {"code": "20637", "name": "관광회사실무실습", "credits": 3},
                {"code": "20638", "name": "리조트실무실습", "credits": 3}
            ]
        },
        '경영학과': {
            "modal-major-i": [
                {"code": "18287", "name": "비즈니스영어(2)", "credits": 2}
            ],
            "modal-major-s": [
                {"code": "01102", "name": "경제학원론", "credits": 3},
                {"code": "19220", "name": "경영과통계", "credits": 3},
                {"code": "19582", "name": "창업전략론", "credits": 3},
                {"code": "01118", "name": "국제기업환경론", "credits": 3},
                {"code": "01159", "name": "원가회계", "credits": 3},
                {"code": "01166", "name": "투자론", "credits": 3},
                {"code": "01122", "name": "경영정보시스템", "credits": 3},
                {"code": "01123", "name": "재무분석론", "credits": 3},
                {"code": "01154", "name": "관리회계", "credits": 3},
                {"code": "06293", "name": "경영전략", "credits": 3},
                {"code": "19690", "name": "Web마케팅", "credits": 3},
                {"code": "01126", "name": "금융시장론", "credits": 3},
                {"code": "01142", "name": "광고론", "credits": 3},
                {"code": "07060", "name": "SCM", "credits": 3},
                {"code": "19224", "name": "다국적기업론", "credits": 3},
                {"code": "20447", "name": "NCS와노사관계론", "credits": 3}
            ]
        },
        '동아시아물류학부': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "17999", "name": "물류프로젝트관리", "credits": 3},
                {"code": "19611", "name": "글로벌기업연구", "credits": 3},
                {"code": "18504", "name": "무역보험론", "credits": 3},
                {"code": "19609", "name": "동아시아비교문화연구", "credits": 3},
                {"code": "21783", "name": "현장실습(3-2)", "credits": 9}
            ]
        },
        '글로벌물류학부': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "02002", "name": "기초일본어(2)", "credits": 3},
                {"code": "02102", "name": "기초중국어(2)", "credits": 3},
                {"code": "20448", "name": "기초일본어회화(2)", "credits": 3},
                {"code": "21125", "name": "글로벌물류입문", "credits": 3},
                {"code": "03011", "name": "기초마케팅", "credits": 3},
                {"code": "17827", "name": "일본어와문화", "credits": 3},
                {"code": "18113", "name": "운송물류론", "credits": 3},
                {"code": "19500", "name": "물류통계론", "credits": 3},
                {"code": "19600", "name": "동아시아해외연수", "credits": 3},
                {"code": "20711", "name": "글로벌구매관리론", "credits": 3},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "07060", "name": "SCM", "credits": 3},
                {"code": "19850", "name": "일본물류기업론", "credits": 3},
                {"code": "20452", "name": "일본물류신문읽기", "credits": 3},
                {"code": "20712", "name": "물류컨설팅방법론", "credits": 3},
                {"code": "19483", "name": "무역학개론", "credits": 3},
                {"code": "19521", "name": "항만물류론", "credits": 3},
                {"code": "19597", "name": "중급일본어(2)", "credits": 3},
                {"code": "21123", "name": "동아시아와평화", "credits": 3},
                {"code": "18118", "name": "무역영어", "credits": 3},
                {"code": "19604", "name": "일본전통문화", "credits": 3}
            ]
        },
        '글로벌물류학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "02002", "name": "기초일본어(2)", "credits": 3},
                {"code": "02102", "name": "기초중국어(2)", "credits": 3},
                {"code": "20448", "name": "기초일본어회화(2)", "credits": 3},
                {"code": "21125", "name": "글로벌물류입문", "credits": 3},
                {"code": "03011", "name": "기초마케팅", "credits": 3},
                {"code": "17827", "name": "일본어와문화", "credits": 3},
                {"code": "18113", "name": "운송물류론", "credits": 3},
                {"code": "19500", "name": "물류통계론", "credits": 3},
                {"code": "19600", "name": "동아시아해외연수", "credits": 3},
                {"code": "20711", "name": "글로벌구매관리론", "credits": 3},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "07060", "name": "SCM", "credits": 3},
                {"code": "19850", "name": "일본물류기업론", "credits": 3},
                {"code": "20452", "name": "일본물류신문읽기", "credits": 3},
                {"code": "20712", "name": "물류컨설팅방법론", "credits": 3},
                {"code": "19483", "name": "무역학개론", "credits": 3},
                {"code": "19521", "name": "항만물류론", "credits": 3},
                {"code": "19597", "name": "중급일본어(2)", "credits": 3},
                {"code": "21123", "name": "동아시아와평화", "credits": 3},
                {"code": "18118", "name": "무역영어", "credits": 3},
                {"code": "19604", "name": "일본전통문화", "credits": 3}
            ]
        },
        '산업경영공학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "07758", "name": "선형대수학", "credits": 3},
                {"code": "07761", "name": "확률과통계", "credits": 3},
                {"code": "20470", "name": "파이썬프로그래밍", "credits": 3},
                {"code": "18472", "name": "공급망관리", "credits": 3},
                {"code": "20975", "name": "작업분석및설계", "credits": 3},
                {"code": "21112", "name": "데이터베이스활용", "credits": 3},
                {"code": "17883", "name": "실험계획의원리", "credits": 3},
                {"code": "18473", "name": "품질관리", "credits": 3},
                {"code": "19560", "name": "물류및시설계획", "credits": 3},
                {"code": "19542", "name": "산업경영공학실무", "credits": 3},
                {"code": "20473", "name": "빅데이터와머신러닝", "credits": 3}
            ]
        },
        '유아교육과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "00151", "name": "아동문학", "credits": 3},
                {"code": "19229", "name": "영유아발달과교육", "credits": 3},
                {"code": "20706", "name": "언어발달장애", "credits": 3},
                {"code": "21359", "name": "아동권리와복지", "credits": 3},
                {"code": "00904", "name": "유아놀이지도", "credits": 3},
                {"code": "00907", "name": "유아수학교육", "credits": 3},
                {"code": "20701", "name": "정서장애아교육", "credits": 3},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "00902", "name": "유아과학교육", "credits": 3},
                {"code": "00959", "name": "유아건강교육", "credits": 3},
                {"code": "18219", "name": "유아교과교육론", "credits": 3},
                {"code": "18357", "name": "아동안전관리", "credits": 3},
                {"code": "18375", "name": "아동관찰및행동연구", "credits": 3},
                {"code": "20643", "name": "영유아프로그램개발및평가", "credits": 3},
                {"code": "20703", "name": "자폐장애교육", "credits": 3},
                {"code": "00901", "name": "부모교육", "credits": 3},
                {"code": "07219", "name": "유아교육기관운영관리", "credits": 3},
                {"code": "18345", "name": "유아교과논리및논술", "credits": 3},
                {"code": "20622", "name": "보육실습II", "credits": 3},
                {"code": "21145", "name": "유아교육현장상담", "credits": 2}
            ]
        },
        '체육교육과': {
            "modal-major-i": [
                {"code": "06216", "name": "체육교수법", "credits": 2}
            ],
            "modal-major-s": [
                {"code": "02503", "name": "체육원리", "credits": 2},
                {"code": "02522", "name": "스키", "credits": 1},
                {"code": "08030", "name": "해부학", "credits": 2},
                {"code": "20331", "name": "스포츠영어(2)", "credits": 3},
                {"code": "21719", "name": "스포츠인성교육", "credits": 2},
                {"code": "21720", "name": "스포츠테크와체육활동설계", "credits": 2},
                {"code": "02504", "name": "운동생리학", "credits": 3},
                {"code": "02563", "name": "테니스(1)", "credits": 2},
                {"code": "06218", "name": "스포츠마사지및의료봉사", "credits": 1},
                {"code": "18221", "name": "체육교과교육론", "credits": 3},
                {"code": "19640", "name": "기계체조", "credits": 2},
                {"code": "19641", "name": "육상경기", "credits": 2},
                {"code": "19688", "name": "노인체육론", "credits": 2},
                {"code": "20768", "name": "수영지도법심화", "credits": 2},
                {"code": "20770", "name": "축구지도법심화", "credits": 2},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "02540", "name": "건강교육", "credits": 3},
                {"code": "02572", "name": "체육측정평가", "credits": 3},
                {"code": "02593", "name": "종합실기(1)", "credits": 1},
                {"code": "20740", "name": "스포츠경영", "credits": 2},
                {"code": "20772", "name": "배구지도법심화", "credits": 2},
                {"code": "20774", "name": "핸드볼지도법심화", "credits": 2},
                {"code": "20776", "name": "배드민턴지도법심화", "credits": 2},
                {"code": "20780", "name": "농구지도법심화", "credits": 2},
                {"code": "02511", "name": "교육무용", "credits": 1},
                {"code": "06223", "name": "여가레크리에이션", "credits": 1},
                {"code": "18239", "name": "운동처방및스포츠영양학", "credits": 2},
                {"code": "19852", "name": "종합무도", "credits": 1},
                {"code": "20742", "name": "종합실기3", "credits": 1},
                {"code": "20743", "name": "종합실기4", "credits": 1},
                {"code": "21793", "name": "스포츠윤리와리더십", "credits": 2}
            ]
        },
        '컴퓨터공학과': {
            "modal-major-i": [
                {"code": "17960", "name": "전공종합설계(1)", "credits": 3}
            ],
            "modal-major-s": [
                {"code": "01364", "name": "컴퓨터네트워크", "credits": 3},
                {"code": "01605", "name": "데이터베이스", "credits": 3},
                {"code": "01305", "name": "컴퓨터구조", "credits": 3},
                {"code": "01308", "name": "운영체제", "credits": 3},
                {"code": "17856", "name": "창의적공학설계", "credits": 3},
                {"code": "20405", "name": "C프로그래밍응용", "credits": 3},
                {"code": "20470", "name": "파이썬프로그래밍", "credits": 3},
                {"code": "21702", "name": "전산수학", "credits": 3},
                {"code": "01353", "name": "논리회로", "credits": 3},
                {"code": "18445", "name": "웹표준기술", "credits": 3},
                {"code": "20461", "name": "자바프로그래밍응용", "credits": 3},
                {"code": "21411", "name": "진로탐색(2)", "credits": 3},
                {"code": "01699", "name": "임베디드시스템", "credits": 3},
                {"code": "20356", "name": "머신러닝", "credits": 3},
                {"code": "03022", "name": "정보보안", "credits": 3},
                {"code": "18264", "name": "엔터프라이즈애플리케이션", "credits": 3}
            ]
        },
        '정보통신공학과': {
            "modal-major-i": [
                {"code": "18241", "name": "공학설계입문", "credits": 3},
                {"code": "01305", "name": "컴퓨터구조", "credits": 3},
                {"code": "01321", "name": "컴퓨터통신", "credits": 3},
                {"code": "06118", "name": "논리회로실습", "credits": 3},
                {"code": "18265", "name": "전자회로설계", "credits": 3},
                {"code": "20894", "name": "종합설계기획", "credits": 1},
                {"code": "20897", "name": "종합설계관리", "credits": 1}
            ],
            "modal-major-s": [
                {"code": "21648", "name": "정보통신수학", "credits": 3},
                {"code": "01728", "name": "영상처리", "credits": 3},
                {"code": "18242", "name": "무선공학", "credits": 3},
                {"code": "20468", "name": "IoT실습과응용", "credits": 3},
                {"code": "20895", "name": "블록체인(1)", "credits": 3},
                {"code": "20896", "name": "디지털시스템실습", "credits": 3},
                {"code": "19654", "name": "취업실무특강", "credits": 3},
                {"code": "20892", "name": "블록체인(2)", "credits": 3}
            ]
        },
        '미디어소프트웨어학과': {
            "modal-major-i": [
                {"code": "01605", "name": "데이터베이스", "credits": 3},
                {"code": "01728", "name": "영상처리", "credits": 3},
                {"code": "20941", "name": "앱프로그래밍(1)", "credits": 3},
                {"code": "17958", "name": "HCI", "credits": 3},
                {"code": "19819", "name": "미디어소프트웨어종합설계(1)", "credits": 3}
            ],
            "modal-major-s": [
                {"code": "01350", "name": "이산수학", "credits": 3},
                {"code": "19815", "name": "C++프로그래밍", "credits": 3},
                {"code": "19816", "name": "미디어소프트웨어기초설계", "credits": 3},
                {"code": "20470", "name": "파이썬프로그래밍", "credits": 3},
                {"code": "20934", "name": "게임/가상현실콘텐츠기획", "credits": 3},
                {"code": "19818", "name": "자바웹프로그래밍(2)", "credits": 3},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "21411", "name": "진로탐색(2)", "credits": 3},
                {"code": "01309", "name": "인공지능", "credits": 3},
                {"code": "01786", "name": "컴퓨터그래픽스(2)", "credits": 3},
                {"code": "20938", "name": "게임엔진(2)", "credits": 3},
                {"code": "20940", "name": "취창업세미나", "credits": 3}
            ]
        },
        '도시디자인정보공학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "19946", "name": "국토및지역계획", "credits": 3},
                {"code": "19947", "name": "도시디자인의이해", "credits": 3},
                {"code": "20062", "name": "지구계획스튜디오", "credits": 3},
                {"code": "20063", "name": "도시방재론", "credits": 3},
                {"code": "20064", "name": "컴퓨터설계", "credits": 3},
                {"code": "20065", "name": "통계분석", "credits": 3},
                {"code": "20066", "name": "스마트도시론", "credits": 3},
                {"code": "19962", "name": "3D공간설계", "credits": 3},
                {"code": "20071", "name": "토지이용이론및실습", "credits": 3},
                {"code": "20072", "name": "도시개발이론및실무", "credits": 3},
                {"code": "20083", "name": "지리정보체계론심화", "credits": 3},
                {"code": "19972", "name": "도시설계론", "credits": 3},
                {"code": "20055", "name": "종합설계", "credits": 3},
                {"code": "20078", "name": "공간계획제도이해", "credits": 3},
                {"code": "20079", "name": "외부공간설계론", "credits": 3},
                {"code": "20080", "name": "도시공간구조론", "credits": 3}
            ]
        },
        '음악학부': {
            "modal-major-i": [
                {"code": "19978", "name": "연주실습(2)", "credits": 1}
            ],
            "modal-major-s": [
                {"code": "01448", "name": "화성학(1)", "credits": 2},
                {"code": "18474", "name": "실기클래스(2)", "credits": 1},
                {"code": "01478", "name": "서양음악사(1)", "credits": 2},
                {"code": "01547", "name": "합창(2)", "credits": 1},
                {"code": "18510", "name": "실기클래스(4)", "credits": 1},
                {"code": "19980", "name": "연주실습(4)", "credits": 1},
                {"code": "20482", "name": "전공영어(2)", "credits": 3},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "01438", "name": "지휘법(2)", "credits": 2},
                {"code": "01549", "name": "합창(4)", "credits": 1},
                {"code": "19982", "name": "연주실습(6)", "credits": 1},
                {"code": "20483", "name": "전통음악의이해", "credits": 2},
                {"code": "20905", "name": "캡스톤디자인(1)", "credits": 3},
                {"code": "21795", "name": "19세기피아노음악", "credits": 2},
                {"code": "01551", "name": "합창(6)", "credits": 1},
                {"code": "01475", "name": "독어딕션", "credits": 2},
                {"code": "17902", "name": "성악의이해(2)", "credits": 2},
                {"code": "20924", "name": "성악전공실기(2)", "credits": 1},
                {"code": "21517", "name": "시창청음성악(심화)", "credits": 2},
                {"code": "01518", "name": "성악문헌(2)", "credits": 2},
                {"code": "20926", "name": "성악전공실기(4)", "credits": 1},
                {"code": "01492", "name": "영어딕션", "credits": 2},
                {"code": "20928", "name": "성악전공실기(6)", "credits": 1},
                {"code": "21087", "name": "오페라워크샵(2)", "credits": 2},
                {"code": "01496", "name": "성악교수법", "credits": 2},
                {"code": "01596", "name": "한국가곡", "credits": 2},
                {"code": "20930", "name": "성악전공실기(8)", "credits": 1},
                {"code": "20931", "name": "성악세미나(2)", "credits": 1},
                {"code": "19988", "name": "피아노기초이론(2)", "credits": 2},
                {"code": "20917", "name": "피아노전공실기(2)", "credits": 1},
                {"code": "21516", "name": "시창청음피아노(심화)", "credits": 2},
                {"code": "01570", "name": "피아노반주실습(2)", "credits": 2},
                {"code": "20474", "name": "피아노연주기법", "credits": 2},
                {"code": "20475", "name": "실용피아노연주", "credits": 2},
                {"code": "20919", "name": "피아노전공실기(4)", "credits": 1},
                {"code": "21498", "name": "유아피아노티칭", "credits": 2},
                {"code": "18518", "name": "고전피아노음악", "credits": 2},
                {"code": "20921", "name": "피아노전공실기(6)", "credits": 1},
                {"code": "19798", "name": "피아노페다고지실습", "credits": 2},
                {"code": "20923", "name": "피아노전공실기(8)", "credits": 1},
                {"code": "20933", "name": "피아노세미나(2)", "credits": 1}
            ]
        },
        '연기예술학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "21412", "name": "서양연극사", "credits": 3},
                {"code": "21415", "name": "문화예술교육개론", "credits": 2},
                {"code": "21421", "name": "신체훈련실습심화", "credits": 2},
                {"code": "21422", "name": "기초연기의확장", "credits": 2},
                {"code": "21417", "name": "문화예술교육현장의이해와실습", "credits": 2},
                {"code": "21425", "name": "중급연기의확대", "credits": 3},
                {"code": "21426", "name": "창작연극워크샵심화", "credits": 3},
                {"code": "21427", "name": "화술심화", "credits": 3},
                {"code": "21428", "name": "무대공간제작실습심화", "credits": 2},
                {"code": "21794", "name": "문학과연극", "credits": 3}
            ]
        },
        '영화영상학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "21307", "name": "고전읽기심화", "credits": 3},
                {"code": "21308", "name": "역사로영화읽기", "credits": 3},
                {"code": "21309", "name": "영상기호와영상스토리텔링", "credits": 3},
                {"code": "21310", "name": "나와영화", "credits": 3},
                {"code": "21314", "name": "심리학으로영화읽기", "credits": 3},
                {"code": "21315", "name": "작가스타일과표현분석", "credits": 3},
                {"code": "21316", "name": "촬영조명심화", "credits": 3},
                {"code": "21317", "name": "시청각적글쓰기(압축,상징,은유)", "credits": 3},
                {"code": "21318", "name": "세트제작워크숍(5분영화만들기)", "credits": 3}
            ]
        },
        '연극영화학부': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "20794", "name": "영화연기디렉팅실습", "credits": 3},
                {"code": "21064", "name": "고전읽기", "credits": 3},
                {"code": "18215", "name": "연극영화논리및논술", "credits": 3},
                {"code": "01590", "name": "가창실습(2)", "credits": 3},
                {"code": "06025", "name": "뮤지컬과대중문화", "credits": 3},
                {"code": "18150", "name": "무대움직임(2)", "credits": 2},
                {"code": "20677", "name": "연극제작2", "credits": 3},
                {"code": "20764", "name": "고급연기", "credits": 3},
                {"code": "21118", "name": "뮤지컬댄스", "credits": 2},
                {"code": "18170", "name": "공연제작프로젝트(2)", "credits": 3},
                {"code": "06035", "name": "단편영화제작(2)", "credits": 3},
                {"code": "18179", "name": "촬영조명연출(2)", "credits": 3},
                {"code": "18191", "name": "영화연출분석", "credits": 3},
                {"code": "20792", "name": "영화와현대철학", "credits": 3},
                {"code": "19829", "name": "단편영화제작(4)", "credits": 3},
                {"code": "20788", "name": "프로듀싱과장편시나리오", "credits": 3}
            ]
        },
        '뷰티디자인학과': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "18479", "name": "뷰티메이크업", "credits": 3},
                {"code": "18480", "name": "웨딩업스타일디자인", "credits": 3},
                {"code": "20715", "name": "기초에스테틱", "credits": 3},
                {"code": "18484", "name": "아트메이크업", "credits": 3},
                {"code": "19672", "name": "헤어실무", "credits": 3},
                {"code": "20716", "name": "에스테틱기기관리", "credits": 3},
                {"code": "21440", "name": "네일테크닉", "credits": 3},
                {"code": "07501", "name": "화장품학", "credits": 3},
                {"code": "18489", "name": "뷰티연구세미나", "credits": 3},
                {"code": "19675", "name": "뷰티위생학", "credits": 3},
                {"code": "19679", "name": "헤어살롱컷", "credits": 3},
                {"code": "20714", "name": "네일살롱트랜드", "credits": 3},
                {"code": "07496", "name": "에스테틱실무세미나", "credits": 3},
                {"code": "18495", "name": "뷰티실무세미나", "credits": 3},
                {"code": "19683", "name": "헤어실무세미나", "credits": 3},
                {"code": "21784", "name": "현장실습(4-2)", "credits": 12}
            ]
        },
        '실용음악과': {
            "modal-major-i": [
                {"code": "21374", "name": "위클리공연실습(2)", "credits": 1},
                {"code": "21376", "name": "위클리공연실습(4)", "credits": 1}
            ],
            "modal-major-s": [
                {"code": "01430", "name": "전공실기(2)", "credits": 1},
                {"code": "20871", "name": "기초화성학", "credits": 2},
                {"code": "21382", "name": "부실기(심화)", "credits": 2},
                {"code": "21383", "name": "대중음악의역사와이해", "credits": 2},
                {"code": "21384", "name": "컴퓨터음악과기보", "credits": 2},
                {"code": "21385", "name": "라이브퍼포먼스응용", "credits": 2},
                {"code": "21796", "name": "로직프로(학생제안과목)", "credits": 3},
                {"code": "01432", "name": "전공실기(4)", "credits": 1},
                {"code": "20883", "name": "작사법", "credits": 2},
                {"code": "21389", "name": "ProTools(심화)", "credits": 2},
                {"code": "21390", "name": "편곡과활용", "credits": 2}
            ]
        },
        '공연음악예술학부': {
            "modal-major-i": [],
            "modal-major-s": [
                {"code": "01434", "name": "전공실기(6)", "credits": 1},
                {"code": "20873", "name": "Percussion앙상블", "credits": 2},
                {"code": "20981", "name": "위클리콘서트(6)", "credits": 0},
                {"code": "20987", "name": "진로와취창업", "credits": 2},
                {"code": "01436", "name": "전공실기(8)", "credits": 1},
                {"code": "20906", "name": "캡스톤디자인(2)", "credits": 2},
                {"code": "20027", "name": "세션레코딩실습(2)", "credits": 2},
                {"code": "20881", "name": "CCM반주법(2)", "credits": 3},
                {"code": "20983", "name": "모던워쉽앙상블(2)", "credits": 3},
                {"code": "20985", "name": "모던워쉽테크놀로지(2)", "credits": 3},
                {"code": "20028", "name": "게임오디오", "credits": 2},
                {"code": "20902", "name": "보컬앙상블(2)", "credits": 2}
            ]
        },
        '융합학부': {
            "modal-major-i": [
                {"code": "20749", "name": "화장품성분학", "credits": 3}
            ],
            "modal-major-s": [
                {"code": "20593", "name": "레이싱대회이벤트실습", "credits": 3},
                {"code": "20144", "name": "미디어사운드디자인기초", "credits": 3},
                {"code": "20199", "name": "미디어와현대사회", "credits": 3},
                {"code": "20988", "name": "미디어리터러시", "credits": 3},
                {"code": "20989", "name": "XR산업의이해", "credits": 3},
                {"code": "21709", "name": "모션캡쳐실습", "credits": 3},
                {"code": "21711", "name": "미디어산업과반도체융합기술", "credits": 3},
                {"code": "20472", "name": "멀티미디어프로그래밍", "credits": 3},
                {"code": "21156", "name": "디지털미디어편집과후반작업", "credits": 3},
                {"code": "21158", "name": "유니티심화", "credits": 3},
                {"code": "17963", "name": "3D모델링", "credits": 3},
                {"code": "20373", "name": "3D콘텐츠제작실습", "credits": 3},
                {"code": "20993", "name": "디지털미디어정책론", "credits": 3},
                {"code": "20994", "name": "실감콘텐츠를위한사운드와음악", "credits": 3},
                {"code": "21160", "name": "언리얼심화", "credits": 3},
                {"code": "20370", "name": "1인미디어제작워크숍(2)", "credits": 3},
                {"code": "20407", "name": "차세대미디어캡스톤디자인(2)", "credits": 3},
                {"code": "19882", "name": "캡스톤디자인", "credits": 3},
                {"code": "20395", "name": "바이오소재경영마케팅", "credits": 3},
                {"code": "20753", "name": "화장품품질검사학및실험", "credits": 3},
                {"code": "20754", "name": "화장품공정학", "credits": 3},
                {"code": "20820", "name": "빅데이터분석과시각화의기초", "credits": 3},
                {"code": "20822", "name": "글로벌전자상거래(CBEC)", "credits": 3},
                {"code": "21349", "name": "관광소셜미디어와빅데이터분석", "credits": 3},
                {"code": "20827", "name": "글로벌SCM", "credits": 3},
                {"code": "20828", "name": "신제품개발실습", "credits": 3},
                {"code": "20591", "name": "레이싱드론경기비행(2)", "credits": 3},
                {"code": "20746", "name": "피부노화학", "credits": 3},
                {"code": "21164", "name": "바이오인체생리학", "credits": 3},
                {"code": "21532", "name": "화장품산업과법규의이해", "credits": 3},
                {"code": "21166", "name": "글로벌화장품산업연구", "credits": 3},
                {"code": "21534", "name": "화장품기획개발", "credits": 3},
                {"code": "20205", "name": "포스트모던문화읽기", "credits": 3},
                {"code": "21659", "name": "엔터테인먼트산업의이해", "credits": 3}
            ]
        }
    };

    const commonCultureC = [
        {code: '21651', name: '기독교로의초대', credits: 3},
        {code: '21652', name: '신앙으로의초대', credits: 3},
        {code: '20956', name: '컴퓨팅사고와코딩기초', credits: 2},
        {code: '19737', name: '기초글쓰기', credits: 2},
        {code: '00074', name: '사회봉사', credits: 1},
        {code: '20163', name: '대학생활과진로', credits: 1},
        {code: '20164', name: '영어커뮤니케이션(1)', credits: 2},
        {code: '20165', name: '영어커뮤니케이션(2)', credits: 2},
        {code: '21923', name: '전공탐색입문(전체)(자율전공학부)', credits: 1}
    ];


    const extraCultureC = {
        '신학과': {code: '21925', name: '전공탐색입문(신학대학)(자율전공학부)', credits: 1},
        '신학부': {code: '21925', name: '전공탐색입문(신학대학)(자율전공학부)', credits: 1},
        '기독교교육상담과': {code: '21925', name: '전공탐색입문(신학대학)(자율전공학부)', credits: 1},
        '문화선교학과': {code: '21925', name: '전공탐색입문(신학대학)(자율전공학부)', credits: 1},

        '국어국문학과': {code: '21926', name: '전공탐색입문(국문대학)(자율전공학부)', credits: 1},
        '영어영문학과': {code: '21926', name: '전공탐색입문(국문대학)(자율전공학부)', credits: 1},
        '중어중문학과': {code: '21926', name: '전공탐색입문(국문대학)(자율전공학부)', credits: 1},

        '국제개발협력학과': {code: '21927', name: '전공탐색입문(사회과학대학)(자율전공학부)', credits: 1},
        '사회복지학과': {code: '21927', name: '전공탐색입문(사회과학대학)(자율전공학부)', credits: 1},
        '행정학과': {code: '21927', name: '전공탐색입문(사회과학대학)(자율전공학부)', credits: 1},
        '행정학부': {code: '21927', name: '전공탐색입문(사회과학대학)(자율전공학부)', credits: 1},

        '관광학과': {code: '21928', name: '전공탐색입문(글로벌경영기술대학)(자율전공학부)', credits: 1},
        '경영학과': {code: '21928', name: '전공탐색입문(글로벌경영기술대학)(자율전공학부)', credits: 1},
        '동아시아물류학부': {code: '21928', name: '전공탐색입문(글로벌경영기술대학)(자율전공학부)', credits: 1},
        '글로벌물류학부': {code: '21928', name: '전공탐색입문(글로벌경영기술대학)(자율전공학부)', credits: 1},
        '산업경영공학과': {code: '21928', name: '전공탐색입문(글로벌경영기술대학)(자율전공학부)', credits: 1},

        '컴퓨터공학과': {code: '21929', name: '전공탐색입문(IT공과대학)(자율전공학부)', credits: 1},
        '정보통신공학과': {code: '21929', name: '전공탐색입문(IT공과대학)(자율전공학부)', credits: 1},
        '미디어소프트웨어학과': {code: '21929', name: '전공탐색입문(IT공과대학)(자율전공학부)', credits: 1},
        '도시디자인정보공학과': {code: '21929', name: '전공탐색입문(IT공과대학)(자율전공학부)', credits: 1},


    };


    function populateModal(modalId) {

        let list = [];
        if (modalId === 'modal-culture-c') {
            // 교필 모달은 공통 + 학과별 추가
            list = commonCultureC.slice(); // 복사
            const extra = extraCultureC[userMajor];
            if (extra) list.push(extra);
        } else {
            // 전필/전선 모달은 기존 recommendations 구조
            list = ((recommendations[userMajor] || {})[modalId] || []).slice();
        }

        // 이수된 과목 위로
        list.sort((a, b) => {
            const aDone = completedNames.includes(a.name);
            const bDone = completedNames.includes(b.name);
            if (aDone && !bDone) return -1;
            if (!aDone && bDone) return 1;
            return 0;
        });

        // 렌더링
        const $tbody = $(`#${modalId} tbody`);
        $tbody.empty();
        list.forEach(item => {
            const done = completedNames.includes(item.name);
            $tbody.append(`
        <tr class="mytr">
          <td class="mytd">${item.code}</td>
          <td class="mytd">${item.name}</td>
          <td class="mytd">${item.credits}</td>
          <td class="mytd ${done ? 'green' : 'red'}">
            ${done ? '이수' : '미이수'}
          </td>
        </tr>
      `);
        });
    }

// 5) 버튼 핸들러
    $(".recommend").off("click").on("click", function () {
        const target = $(this).attr("data-target");
        if (target === "modal-culture-e") {
            window.location.href = "/recommend";
        } else {
            populateModal(target);
            $(`#${target}`).fadeIn();
        }
    });

});

