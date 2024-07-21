function submitAuthForm() {
        var userId = $('#id').val();
        var password = $('#pw').val();
        var errorMessage = '';

        if (!userId) {
            errorMessage = '아이디를 입력해주세요.';
        } else if (!password) {
            errorMessage = '비밀번호를 입력해주세요.';
        }

        if (errorMessage) {
            $('#error-message1').text(errorMessage);
            return false;
        }

        $('#error-message').text('');

        $('#m1').show();

        $.post('/api/auth', {userId: userId, password: password}, function(response) {
            $('#m1').hide();

            if (response.is_auth) {
                var form = $('<form>', {
                    'action': '/register',
                    'method': 'post'
                });

                $.each(response, function(key, value) {
                    form.append($('<input>', {
                        'type': 'hidden',
                        'name': key,
                        'value': value
                    }));
                });

                form.appendTo('body').submit();
            } else {
                alert('❌재학생 인증에 실패하였습니다.❌ ');
                $('#error-message1').text('❌인증 실패❌');
                $('#error-message2').text('⚠️입력정보를 다시 확인해주세요!⚠️');
            }
        });

        return false;
    }

    function agreeFunc() {
        var chk = document.getElementById('agree');
        var tag;
        if (chk.checked) {
            tag = "<input type='submit' class='button--moema2' value='인증하기' style='margin-top: 1rem;'>";
        } else {
            tag = "<div class='login_btn_default'>이용약관에 동의해주세요.</div>";
        }
        document.getElementById("login_btn").innerHTML = tag;
    }


    function checkCapsLock(event) {
    if (event.getModifierState("CapsLock")){
    document.getElementById("capslock-message").innerText = "⚠️CapsLock 활성화⚠️"
    } else {
         document.getElementById("capslock-message").innerText = ""
         }
    }

    function togglePassword() {
            const passwordField = document.getElementById("pw");
            const passwordFieldType = passwordField.getAttribute("type");
            const toggleIcon = document.querySelector(".show-password");
            if (passwordFieldType === "password") {
                passwordField.setAttribute("type", "text");
                toggleIcon.textContent = "🐵";
            } else {
                passwordField.setAttribute("type", "password");
                toggleIcon.textContent = "🙈";
            }
        }

