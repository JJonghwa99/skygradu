let addedCourses = [];
let deletedCourses = [];

function ajax_conn() {
    const keyword = document.getElementById('s_num').value.trim(); // 공백 제거

    // keyword가 비어있으면 요청하지 않음
    if (!keyword) {
        document.getElementById('result_div').style.display = 'none';
        document.getElementById('no_result_message').style.display = 'block';
        return;
    }

    fetch(`/lectures/search?keyword=${encodeURIComponent(keyword)}`)
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector('#customTable tbody');
            const resultDiv = document.getElementById('result_div');

            if (data.length > 0) {
                tbody.innerHTML = data.map(lecture => `
                <tr>
                    <td style="width:9%">커스텀</td>
                    <td style="width:9%">${lecture.semesterCompleted}</td>
                    <td style="width:9%">${lecture.lectureCode}</td>
                    <td style="width:36%">${lecture.courseName}</td>
                    <td style="width:9%">
                        <select class="course-type">
                            <option>전필</option>
                            <option>전선</option>
                            <option>교필</option>
                            <option>교선</option>
                            <option>일선</option>
                        </select>
                    </td>
                    <td style="width:6%">${lecture.credits}</td>
                    <td style="width:6%"><button class="add_btn" onclick="addRow(this)">추가</button></td>
                </tr>
                `).join('');
                resultDiv.style.display = 'block';
                document.getElementById('no_result_message').style.display = 'none';
            } else {
                resultDiv.style.display = 'none';
                document.getElementById('no_result_message').style.display = 'block';
            }
        })
        .catch(error => console.error('Error fetching data:', error));
}



// 행 추가 기능
function addRow(button) {
    const row = button.closest('tr'); // 현재 클릭된 버튼의 부모 행 가져오기
    const lectureCode = row.cells[2].innerText; // 과목 코드
    const courseName = row.cells[3].innerText; // 과목 이름
    const selectedCourseType = row.querySelector('.course-type').value; // 선택된 과목 유형
    const credits = row.cells[5].innerText; // 학점

    // 중복 검사: 기존 테이블에서 강의명과 이수구분이 동일하면 빠꾸
    const existingRows = Array.from(document.querySelectorAll('#myTable tbody tr'));
    const isDuplicate = existingRows.some(existingRow =>
        existingRow.cells[3].innerText === selectedCourseType &&
        existingRow.cells[2].innerText === courseName
    );

    if (isDuplicate) {
        alert('이미 추가된 과목입니다.');
        return;
    }

    // 추가된 데이터 배열에 저장
    addedCourses.push({
        courseName: courseName,
        courseType: selectedCourseType,
        semesterCompleted: "커스텀",
        credits: parseInt(credits, 10)
    });

    // 데이터를 정리하여 새 행 생성
    const newRow = document.createElement('tr'); // 새로운 <tr> 요소 생성

    newRow.innerHTML = `
        <td style="width:9%">커스텀</td>
        <td style="width:9%">커스텀 </td>
        <td style="width:40%">${courseName}</td>
        <td style="width:9%">${selectedCourseType}</td> <!-- 선택된 값만 텍스트로 표시 -->
        <td style="width:6%">${credits}</td>
        <td style="width:6%"><button class="del_btn" onclick="deleteRow(this)">삭제</button></td>
    `;

    // 새로 추가된 행의 텍스트 색상 변경
    Array.from(newRow.cells).forEach(cell => {
        cell.style.color = '#1ac06d'; // 텍스트 색상 설정
    });

    // 테이블에 새 행 추가
    const tableBody = document.querySelector('#myTable tbody');
    tableBody.insertBefore(newRow, tableBody.firstChild);

}

//추가한 데이터 행 지우기 폼에서도 지움
function deleteRow(button) {
    const row = button.closest('tr'); // 삭제할 행 가져오기
    const lectureName = row.cells[2].innerText; // 과목 이름

    // 추가된 데이터 배열에서 제거
    addedCourses = addedCourses.filter(course => course.courseName !== lectureName);

    row.remove();
}


//데이터베이스에 저장된 커스텀데이터 삭제 (저장버튼 눌러야 데이터베이스에 적용)
function del_row_old(button) {
    let row = button.closest("tr");
    if (row) {
        row.style.display = "none";
    }

    const courseName = row.cells[2].innerText; // 과목명

    // 삭제된 데이터 배열에 저장
    deletedCourses.push({ courseName });
}

function saveCourses() {
    // 데이터 누락되면 전송안함
    const filteredAddedCourses = addedCourses.filter(course => Object.values(course).every(value => value));
    const filteredDeletedCourses = deletedCourses.filter(course => Object.values(course).every(value => value));

    // 데이터가 모두 비어있으면 아무것도 안하고 /mypage로 이동
    if (filteredAddedCourses.length === 0 && filteredDeletedCourses.length === 0) {
        alert("❌저장할 데이터가 없어요!😥");
        window.location.href = "/mypage";
        return;
    }

    fetch("/custom/save", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ added: filteredAddedCourses, deleted: filteredDeletedCourses })
    })
        .then(response => {
            if (response.ok) {
                alert("✅커스텀 내역을 저장했어요!😁");
                window.location.href = "/mypage";
            } else {
                alert("❌저장 실패 다시 시도해 주세요.");
                console.error("서버 응답 오류:", response.statusText);
            }
        })
        .catch(error => {
            console.error("네트워크 오류 발생:", error);
            alert("네트워크 오류로 저장에 실패했습니다.");
        });
}