// 비밀번호 토글
function togglePassword3(inputId, iconClass) {
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

// 모달 열기/닫기
function toggleModal(modalId, action) {
    const modal = document.getElementById(modalId);

    if (action === 'open') {
        modal.style.display = 'block';
    } else if (action === 'close') {
        modal.style.display = 'none';
        // m2 모달 닫을 때 오류 메시지 초기화
        if (modalId === 'm2') {
            document.getElementById('error-message1').textContent = '';
        }
    }
}

// 엑셀 파일 직접 업로드 버튼
document.getElementById('b4').addEventListener('click', () => {
    document.getElementById('excelUpload').click();
});

// 엑셀 파일 선택 후 업로드
document.getElementById('excelUpload').addEventListener('change', event => {
    const file = event.target.files[0];
    if (!file) return;

    if (confirm(
        "📂 엑셀파일을 업로드 할까요?\n\n" +
        "✅ 커스텀 된 수업만 남기고 업데이트 할게요😁\n\n" +
        "✅ 커스텀 된 수업의 수강내역이 발견되면 그 수업도 업데이트 합니다!"
    )) {
        const formData = new FormData();
        formData.append("file", file);

        fetch("/api/excel/upload", {
            method: "POST",
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert("업로드 실패 ❌\n본인의 파일이 맞는지 확인해주세요😥");
                } else {
                    alert(`이수과목 업로드 완료!\n${data.message}`);
                }
                location.reload();
            })
            .catch(error => {
                alert("업로드 요청 중 오류 발생: " + error);
            });
    }
});

// 자동 이수과목 업데이트
function submitAutoExcel() {
    const userId = document.querySelector('input[name="userId"]').value;
    const password = document.getElementById('PortalPassword3').value;
    const errorMessage = document.getElementById('error-message');
    const loadingModal = document.getElementById('m6');

    if (!password) {
        errorMessage.textContent = "😰 비밀번호를 입력해주세요.";
        return;
    }

    errorMessage.textContent = "";
    loadingModal.style.display = "block";

    fetch("/api/excel/auto", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ userId, password })
    })
        .then(response => response.json())
        .then(data => {
            loadingModal.style.display = "none";
            if (data.is_auth === false) {
                errorMessage.textContent = "⚠️ 입력정보를 다시 확인해주세요! ⚠️";
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

// 모든 모달 닫기
function closeAllModals() {
    ['m5', 'm6'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = 'none';
    });
}

// 학과 정보 업데이트 (포털)
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
            toggleModal('m7', 'close');
            toggleModal('m1', 'close');

            if (data.status === 'success') {
                alert("✅ 학과 정보 업데이트 완료!");
                location.reload();
            } else if (data.status === 'no_change') {
                alert("🤔 변경된 정보가 없어서 업데이트 되지 않았어요");
                location.reload();
            } else {
                alert("😥 업데이트 중 오류가 발생했습니다. ❌");
            }
        })
        .catch(error => {
            toggleModal('m7', 'close');
            alert('네트워크 오류가 발생했습니다: ' + error.message);
        });
}

// 기이수과목 데이터 유무 확인 후 졸업요건 검사 이동
document.getElementById("test").addEventListener("click", () => {
    const rows = document.querySelectorAll("#long_table tr");
    let hasValidData = Array.from(rows).slice(1).some(row =>
        Array.from(row.cells).some(cell => cell.textContent.trim() !== "")
    );

    if (hasValidData) {
        window.location.href = "/graduation-check";
    } else {
        alert("❌ 기이수과목 데이터를 업데이트 해야만 검사가 가능해요! 😥");
    }
});

// Minor/복전 설정 모달 제어
function openMinorModal() {
    document.getElementById('minorModal').style.display = 'block';
}
function closeMinorModal() {
    document.getElementById('minorModal').style.display = 'none';
}

// Minor/복전 정보 변경 요청
function submitMinorUpdate() {
    const year         = document.getElementById('minorYear').value;
    const electiveType = document.getElementById('minorElective').value;
    const minorDept    = document.getElementById('minorDept').value;

    fetch('/mypage/updateMinor', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `year=${year}&electiveType=${encodeURIComponent(electiveType)}&minorDept=${encodeURIComponent(minorDept)}`
    })
        .then(res => res.json())
        .then(json => {
            if (json.status === 'success') {
                alert('선택전공 정보가 변경되었습니다.');
                // 모델이 바뀐 elective/minorDept를 반영하기 위해 전체 새로고침
                window.location.href = '/mypage';
            } else {
                alert('변경에 실패했습니다.');
            }
        })
        .catch(err => {
            console.error('Minor update error:', err);
            alert('서버 오류가 발생했습니다.');
        });
}

// DOMContentLoaded 시 이벤트 연결
document.addEventListener('DOMContentLoaded', () => {
    // 비밀번호 토글 아이콘
    ['show-password1','show-password2','show-password3','show-password4','show-password5','show-password7']
        .forEach(cls => {
            const el = document.querySelector(`.${cls}`);
            if (el) el.addEventListener('click', () => togglePassword(el.previousElementSibling.id, cls));
        });

    // 모달 열기 버튼
    document.getElementById('b2')?.addEventListener('click', () => toggleModal('m2', 'open'));
    document.getElementById('b5')?.addEventListener('click', () => toggleModal('m5', 'open'));

    // 모달 닫기 버튼
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const parent = btn.closest('.modal');
            if (parent) parent.style.display = 'none';
        });
    });

    // Minor 모달 버튼
    document.getElementById('openMinorModalBtn')?.addEventListener('click', openMinorModal);
    document.querySelectorAll('#closeMinorModalBtn').forEach(btn => {
        btn.addEventListener('click', closeMinorModal);
    });
    document.getElementById('submitMinorModalBtn')?.addEventListener('click', submitMinorUpdate);
});