<!--(M2) 비번변경 모달 스크립트-->
function verifyCurrentPassword1() {
    var currentPassword = document.getElementById('currentPassword').value;

    fetch('/checkPW', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ currentPassword: currentPassword })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById('passwordUpdateModalStep1').style.display = 'none';
            document.getElementById('passwordUpdateModalStep2').style.display = 'block';
            document.getElementById('error-message1').textContent = '';
        } else {
            document.getElementById('error-message1').textContent = '❌현재 비밀번호가 일치하지 않습니다.😰';
            document.getElementById('currentPassword').focus();
        }
    });
}

function changePassword() {
    var currentPassword = document.getElementById('currentPassword').value;
    var newPassword = document.getElementById('newPassword').value;
    var confirmNewPassword = document.getElementById('confirmNewPassword').value;

    if (!newPassword || !confirmNewPassword) {
        document.getElementById('error-message2').textContent = '😰비밀번호를 입력해주세요.';
        document.getElementById('newPassword').focus();
        return;
    }

    if (newPassword !== confirmNewPassword) {
        document.getElementById('error-message2').textContent = '😰새 비밀번호가 일치하지 않습니다.❌';
        document.getElementById('newPassword').focus();
        return;
    }

    if (newPassword.length < 8) {
        document.getElementById('error-message2').textContent = '😰비밀번호는 8자 이상이어야 합니다.❌';
        document.getElementById('newPassword').focus();
        return;
    }
    if(newPassword==currentPassword){
        document.getElementById('error-message2').textContent = '😰현재 비밀번호와 같습니다.❌';
        document.getElementById('newPassword').focus();
        return;
    }


    fetch('/changePW', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ currentPassword: currentPassword, newPassword: newPassword })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('비밀번호가 성공적으로 변경되었습니다.😊');
            window.location.href = '/mypage';
        } else {
            document.getElementById('error-message2').textContent = '⚠️비밀번호 변경 중 오류가 발생했습니다.😰';
        }
    });
}