    function check_up() {
        const pw1 = document.getElementById('pw1');
        const pw2 = document.getElementById('pw2');
        const pw1Value = pw1.value;
        const pw2Value = pw2.value;

        if (!pw1Value) {
            alert('😢 비밀번호를 입력해주세요.');
            pw1.focus();
            return false;
        }
        if (pw1Value !== pw2Value) {
            alert('😢 비밀번호가 일치하지 않습니다.');
            pw1.focus();
            return false;
        }
        return true;
    }

    $(document).ready(function() {
        $('#register-form').submit(function(event) {
            event.preventDefault(); // 폼 기본 제출을 막음
            if (check_up()) { // 비밀번호 검증 통과 시에만 AJAX 요청 실행
                $.ajax({
                    url: '/member',
                    type: 'POST',
                    data: {
                        userName: $('input[name="userName"]').val(),
                        studentID: $('input[name="studentID"]').val(),
                        major: $('input[name="major"]').val(),
                        userEmail: $('input[name="userEmail"]').val(),
                        password1: $('#pw1').val(),
                        portalID: $('input[name="portalID"]').val()
                    },
                    success: function(response) {
                        if(response === "success"){
                            window.location.href = '/success';
                        } else if (response === "error1") {
                            $('#error-message').text('😰이미 가입된 학번입니다!❌');
                        } else if (response === "error2") {
                            $('#error-message').text('😰비밀번호가 너무 짧아요.');
                        } else {
                            $('#error-message').text('😰회원가입에 실패했어요.');
                        }
                    },
                    error: function() {
                        $('#error-message').text('😰회원가입에 실패했어요.');
                    }
                });
            }
        });
    });

    <!--비밀번호 보이기 스크립트-->
        function togglePassword(inputId, iconClass) {
        const passwordField = document.getElementById(inputId);
        const passwordFieldType = passwordField.getAttribute("type");
        const toggleIcon = document.querySelector(`.${iconClass}`);

        if (passwordFieldType === "password") {
            passwordField.setAttribute("type", "text");
            toggleIcon.textContent = "🐵";
        } else {
            passwordField.setAttribute("type", "password");
            toggleIcon.textContent = "🙈";
        }
    }


    /*function togglePassword1() {
        const passwordField = document.getElementById("pw1");
        const passwordFieldType = passwordField.getAttribute("type");
        const toggleIcon = document.querySelector(".show-password1");
        if (passwordFieldType === "password") {
            passwordField.setAttribute("type", "text");
            toggleIcon.textContent = "🐵";
        } else {
            passwordField.setAttribute("type", "password");
            toggleIcon.textContent = "🙈";
        }
    }

    function togglePassword2() {
        const passwordField = document.getElementById("pw2");
        const passwordFieldType = passwordField.getAttribute("type");
        const toggleIcon = document.querySelector(".show-password2");
        if (passwordFieldType === "password") {
            passwordField.setAttribute("type", "text");
            toggleIcon.textContent = "🐵";
        } else {
            passwordField.setAttribute("type", "password");
            toggleIcon.textContent = "🙈";
        }
    }*/