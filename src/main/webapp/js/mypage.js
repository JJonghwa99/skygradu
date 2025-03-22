
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

    function toggleModal(modalId, action) {
    const modal = document.getElementById(modalId);

    if (action === 'open') {
    modal.style.display = 'block';
} else if (action === 'close') {
    modal.style.display = 'none';

    if (modalId === 'm2') {
    document.getElementById('error-message1').textContent = '';
}
}
}

    document.getElementById('b4').addEventListener('click', function() {
    document.getElementById('excelUpload').click();
});

    document.getElementById('excelUpload').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (!file) return;

    if (confirm("📂엑셀파일을 업로드 할까요?\n\n"+
        "✅커스텀 된 수업만 남기고 업데이트 할게요😁\n\n"+
        "✅커스텀 된 수업의 수강내역이 발견되면 그 수업도 업데이트 합니다!")) {
    const formData = new FormData();
    formData.append("file", file);

    fetch("/api/excel/upload", {
    method: "POST",
    body: formData
})
    .then(response => response.json())
    .then(data => {
    console.log("서버 응답:", data);
    if (data.error) {
    alert("업로드 실패 ❌\n 본인의 파일이 맞는지 확인해주세요😥");
} else {
    alert(`이수과목 업로드 완료!\n${data.message}`);
}
    window.location.reload();
})
    .catch(error => {
    alert("업로드 요청 중 오류 발생: " + error);
});
}
});

    function submitAutoExcel() {
    const userId = document.querySelector('input[name="userId"]').value;
    const password = document.getElementById('PortalPassword3').value;
    const errorMessage = document.getElementById('error-message');
    const mainModal = document.getElementById('m5');
    const loadingModal = document.getElementById('m6');

    if (!password) {
    errorMessage.textContent = "😰비밀번호를 입력해주세요.";
    return;
}

    errorMessage.textContent = "";
    loadingModal.style.display = "block";

    fetch("/api/excel/auto", {
    method: "POST",
    headers: {
    "Content-Type": "application/x-www-form-urlencoded",
},
    body: new URLSearchParams({ userId, password }),
})
    .then(response => response.json())
    .then(data => {
    if (data.is_auth === false) {
    loadingModal.style.display = "none";
    errorMessage.textContent = "⚠️입력정보를 다시 확인해주세요!⚠️";
} else {
    alert("이수 과목 업데이트 완료!");
    closeAllModals();
    location.reload();
}
})
    .catch(error => {
    console.error("Error:", error);
    loadingModal.style.display = "none";
    alert("서버 오류가 발생했습니다.");
});
}

    function closeAllModals() {
    document.getElementById('m5').style.display = "none";
    document.getElementById('m6').style.display = "none";
}
    function updateMajor() {
        const userId = document.getElementById('PortalUserId').value;
        const password = document.getElementById('PortalPassword').value;

        toggleModal('m7', 'open');

        fetch('/updateMajor', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `userId=${encodeURIComponent(userId)}&password=${encodeURIComponent(password)}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'no_login') {
                    const errorMessage = document.getElementById('error-message5');
                    errorMessage.textContent = "⚠️입력정보를 다시 확인해주세요!⚠️";
                    errorMessage.style.display = "block";
                    toggleModal('m7', 'close');
                } else {
                    toggleModal('m1', 'close');
                    toggleModal('m7', 'close');

                    if (data.status === 'success') {
                        alert("✅학과 정보 업데이트 완료!");
                        location.reload();
                    } else if (data.status === 'no_change') {
                        alert("🤔변경된 정보가 없어서 업데이트 되지 않았어요");
                        location.reload();
                    } else if (data.status === 'error') {
                        alert("😥업데이트 중 오류가 발생했습니다.❌");
                    }
                }
            })
            .catch(error => {
                toggleModal('m7', 'close');
                alert('네트워크 오류가 발생했습니다: ' + error.message);
            });
    }