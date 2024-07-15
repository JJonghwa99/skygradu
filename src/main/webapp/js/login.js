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