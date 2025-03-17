function checkCapsLock(event) {
    if (event.getModifierState("CapsLock")) {
        document.getElementById("capslock-message").innerText = "⚠️CapsLock 활성화⚠️"
    } else {
        document.getElementById("capslock-message").innerText = ""
    }
}

function togglePassword() {
    const passwordField = document.getElementById("password1");
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


    function openM2() {
    document.getElementById('m2').style.display = 'block';
}

    function closeM2() {
    document.getElementById('m2').style.display = 'none';
    document.getElementById('error-message1').textContent = '';
}


    function togglePassword2(inputId, iconClass) {
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
    function portalCheck() {
    const userId = document.getElementById("id").value;
    const password = document.getElementById("currentPassword").value;
    const errorMessage = document.getElementById("error-message1");

    fetch('/login/checkPortal', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: new URLSearchParams({userId, password})
})
    .then(response => response.json())
    .then(data => {
    if (data.status === "exists") {
    document.getElementById("passwordUpdateModalStep1").style.display = "none"; // step1 숨김
    document.getElementById("passwordUpdateModalStep2").style.display = "block"; // step2 표시
    errorMessage.textContent = "";
} else if (data.status === "no_member") {
    errorMessage.textContent = "😰가입되지 않은 계정입니다! 회원가입 후 진행해주세요";
} else if (data.status === "invalid_credentials") {
    errorMessage.textContent = "⚠️입력정보를 다시 확인해주세요!⚠️";
} else {
    errorMessage.textContent = "😰서버 에러가 발생했습니다. 잠시 후 다시 시도해주세요.❌";
}
})
    .catch(() => {
    errorMessage.textContent = "서버와 통신 중 오류가 발생했습니다.";
});
}

    function changeLoginPW() {
    const newPassword = document.getElementById("newPassword").value;
    const confirmNewPassword = document.getElementById("confirmNewPassword").value;
    const errorMessage = document.getElementById("error-message2");

    if (newPassword.length < 8) {
    errorMessage.textContent = "😰비밀번호는 8자 이상이어야 합니다.❌";
    return;
}

    if (newPassword !== confirmNewPassword) {
    errorMessage.textContent = "😰새 비밀번호가 일치하지 않습니다.❌";
    return;
}

    fetch('/login/changePW', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({newPassword, confirmNewPassword})
})
    .then(response => response.json())
    .then(data => {
    if (data.status === "success") {
    alert("✅비밀번호 변경완료!");
    location.reload();
} else if (data.status === "session_expired") {
    errorMessage.textContent = "😰세션이 만료되었습니다. 다시 시도해주세요.❌";
} else {
    errorMessage.textContent = data.message || "😰비밀번호 변경 중 오류가 발생했습니다.❌";
}
})
    .catch(() => {
    errorMessage.textContent = "😰서버와 통신 중 오류가 발생했습니다.❌";
});
}
