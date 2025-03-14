package com.SkyGradU.SkyGradU.User.Courses;

import com.SkyGradU.SkyGradU.User.member.Member;
import com.SkyGradU.SkyGradU.User.member.MemberRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AutoExcelService {
    private static final String DOWNLOAD_DIR = System.getProperty("user.home") + "/Downloads";  // 기본 다운로드 경로
    private static final String UPLOAD_URL = "http://localhost:8080/api/excel/upload2"; // 업로드할 API 주소
    private final MemberRepository memberRepository;

    public AutoExcelService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Map<String, Object> autoExcelUpdate(String userId, String password) {
        Map<String, Object> response = new HashMap<>();

        ChromeOptions options = new ChromeOptions();
        /*options.addArguments("--headless=new");*/ // 창을 띄우지 않음
        HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_DIR);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);

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



                    Optional<Member> member = memberRepository.findByPortalID(userId);
                    String filename = member.get().getStudentID();

                    File downloadedFile = findDownloadedFile();
                    if (downloadedFile != null) {
                        // 파일명 변경
                        File renamedFile = renameFile(downloadedFile, filename);

                        // 파일 업로드
                        boolean uploadSuccess = uploadFile(renamedFile);
                        response.put("upload_success", uploadSuccess);

                        // 파일 삭제
                        if (renamedFile.exists()) {
                            renamedFile.delete();
                        }
                    }
                } else {
                    response.put("error", "'성적정보' 페이지 이동 실패");
                }
            } else {
                response.put("is_auth", false);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            response.put("error", e.getMessage());
        } finally {
            driver.quit();
        }
        return response;
    }
    private File renameFile(File oldFile, String filename) {
        File newFile = new File(DOWNLOAD_DIR, filename + ".xlsx");
        try {
            Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
            return oldFile;
        }
    }


    private boolean uploadFile(File file) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);



        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(UPLOAD_URL, HttpMethod.POST, requestEntity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }

    private File findDownloadedFile() {
        File dir = new File(DOWNLOAD_DIR);
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".xls") || name.endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            return null;
        }

        return Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

}