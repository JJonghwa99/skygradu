package com.SkyGradU.SkyGradU.User;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    public Map<String, Object> authenticate(String userId, String password) {
        Map<String, Object> response = new HashMap<>();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // 창을 띄우지 않음

        WebDriver driver = new ChromeDriver(options);

        try {
            Connection.Response loginResponse = Jsoup.connect("https://www.sungkyul.ac.kr/portalLogin/skukr/loginProcess.do")
                    .data("userId", userId)
                    .data("password", password)
                    .method(Connection.Method.POST)
                    .execute();

            // 로그인 성공 여부 확인
            if (loginResponse.body().contains("\"result\":\"SUCCESS\"")) {

                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

                // 로그인 페이지로 이동
                driver.get("https://www.sungkyul.ac.kr/portalLogin/skukr/portalLoginForm.do");

                // 로그인 폼 채우기
                WebElement userIdField = driver.findElement(By.name("userId"));
                WebElement passwordField = driver.findElement(By.name("password"));

                userIdField.sendKeys(userId);
                passwordField.sendKeys(password);

                // 로그인 버튼 클릭 (ID 사용)
                WebElement loginButton = driver.findElement(By.id("btn-login"));
                loginButton.click();

                // 로그인 후 쿠키 저장
                Map<String, String> loginCookies = new HashMap<>();
                for (Cookie cookie : driver.manage().getCookies()) {
                    loginCookies.put(cookie.getName(), cookie.getValue());
                }

                // 로그인 후 정보변경 페이지에서 이메일 부분의 값을 userEmail에 저장 후 다시 이전 페이지로 돌아감
                driver.get("https://www.sungkyul.ac.kr/portalLogin/skukr/portalModifyForm.do");

                // 이메일 값 추출
                WebElement emailField = driver.findElement(By.name("email"));
                String userEmail = emailField.getAttribute("value");

                // 이전 페이지로 돌아가기
                driver.navigate().back();

                // 로그인 후 화면에서 다음 페이지로 넘어가는 버튼 누르기
                WebElement nextPageLink = driver.findElement(By.cssSelector("a[href='https://success.sungkyul.ac.kr/sso/main.aspx']"));
                nextPageLink.click();

                driver.get("https://success.sungkyul.ac.kr/Career/startpage.aspx");

                // 버튼 클릭 후 대기
                Thread.sleep(1500);

                // 프로필 페이지 접근
                driver.get("https://success.sungkyul.ac.kr/Office/Teacher/ProfileGetData.aspx?mode=6&pid=N");
                String profilePageHtml = driver.getPageSource();

                // 버튼 클릭 후 대기
                Thread.sleep(1500);

                // userName을 포함한 요소 찾기
                WebElement userNameElement = driver.findElement(By.cssSelector("div[style='font-size:11pt;font-weight:bold']"));
                String userName = userNameElement.getText();

                // userName 위의 요소 (StudentID) 찾기
                WebElement studentIDElement = userNameElement.findElement(By.xpath("preceding-sibling::div[1]"));
                String studentID = studentIDElement.getText();

                // userName 아래의 요소 (major) 찾기
                WebElement majorElement = userNameElement.findElement(By.xpath("following-sibling::div[1]"));
                String major = majorElement.getText();

                response.put("is_auth", true);
                response.put("userName", userName);
                response.put("studentID", studentID);
                response.put("major", major);
                response.put("userEmail", userEmail);
            } else {
                response.put("is_auth", false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.put("is_auth", false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            response.put("is_auth", false);
        } finally {
            driver.quit();
        }
        return response;
    }
}
