    <!--(M3) 회원탈퇴 모달 스크립트-->
    function verifyCurrentPassword2() {
    var currentPassword = document.getElementById('currentPassword1').value;

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
            document.getElementById('WithdrawalModalStep1').style.display = 'none';
            document.getElementById('WithdrawalModalStep2').style.display = 'block';
            document.getElementById('error-message3').textContent = '';
        } else {
            document.getElementById('error-message3').textContent = '❌현재 비밀번호가 일치하지 않습니다.😰';
        }
    });
}
async function deleteAccount() {
        const response = await fetch('/member/delete', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert('탈퇴 성공! 다음에 또 봐요🥹');
            document.getElementById('error-message4').textContent = '';
            window.location.href = '/';

        } else {
            document.getElementById('error-message4').textContent = '❌회원탈퇴에 실패했습니다.😰';
        }
    }
