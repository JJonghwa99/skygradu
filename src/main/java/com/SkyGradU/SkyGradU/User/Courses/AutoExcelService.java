package com.SkyGradU.SkyGradU.User.Courses;

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
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AutoExcelService {
    public Map<String, Object> autoExcelUpdate(String userId, String password) {
        Map<String, Object> response = new HashMap<>();

        ChromeOptions options = new ChromeOptions();
        /*options.addArguments("--headless=new");*/ // 창을 띄우지 않음

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

                // 로그인 버튼 클릭
                WebElement loginButton = driver.findElement(By.id("btn-login"));
                loginButton.click();

                // 로그인 후 쿠키 저장 (필요한가?)
                Map<String, String> loginCookies = new HashMap<>();
                for (Cookie cookie : driver.manage().getCookies()) {
                    loginCookies.put(cookie.getName(), cookie.getValue());
                }
                String originalWindow = driver.getWindowHandle();

                //sky시스템 접속
                WebElement nextPageLink = driver.findElement(By.cssSelector("a[href='https://sky.sungkyul.ac.kr:444/sso/index.jsp']"));
                nextPageLink.click();

                //요소 뜰 때까지 대기
                Thread.sleep(3300);

                Set<String> windowHandles = driver.getWindowHandles();
                for (String handle : windowHandles) {
                    if (!handle.equals(originalWindow)) {
                        driver.switchTo().window(handle);
                        break;
                    }
                }


                List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(), '성적정보')]"));
                if (!elements.isEmpty()) {
                    WebElement 성적정보 = elements.get(0);
                    성적정보.click();

                    WebElement 개인별학기성적조회 = driver.findElement(By.xpath("//span[text()='개인별학기성적조회']"));
                    개인별학기성적조회.click();

                    driver.switchTo().frame("tabControl_contents_90103030_body");
                    WebElement 이수구분별성적조회 = driver.findElement(By.xpath("//td[@id='tgExt3_center' and contains(text(), '이수구분별성적조회')]"));
                    이수구분별성적조회.click();

                    Thread.sleep(3000);
                    Set<String> allWindows = driver.getWindowHandles();
                    String lastWindow = "";


                    for (String window : allWindows) {
                        driver.switchTo().window(window);
                        lastWindow = window;
                    }

                    for (String window : allWindows) {
                        if (!window.equals(lastWindow)) {
                            driver.switchTo().window(window);
                            driver.close();
                            System.out.println("❎ 창 닫음: " + window);
                        }
                    }

                    driver.switchTo().window(lastWindow);
                    driver.switchTo().frame("rex_ifrmRexPreview_new");
                    WebElement 엑셀저장버튼 = driver.findElement(By.xpath("//*[@title='엑셀저장']"));
                    엑셀저장버튼.click();


                } else {
                    System.out.println("❌ '성적정보' 클릭 실패 (요소가 비어 있음).");
                }
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
            /*driver.quit();*/
        }
        return response;
    }
}