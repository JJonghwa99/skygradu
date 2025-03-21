package com.SkyGradU.SkyGradU;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import org.springframework.stereotype.Component;

@Component
public class SkyAuth {

    private static final String LOGIN_URL = "https://www.sungkyul.ac.kr/portalLogin/skukr/loginProcess";
    private static final String AUTH_AFTER_URL = "https://www.sungkyul.ac.kr/portalLogin/skukr/loginAuthAfter.do?returnUrl=https://success.sungkyul.ac.kr/sso/main.aspx";
    private static final String RETURN_URL = "https://success.sungkyul.ac.kr/sso/main.aspx?pmi-sso-return2=";
    private static final String PROFILE_DATA_URL = "https://success.sungkyul.ac.kr/Office/Teacher/ProfileGetData.aspx?mode=6&pid=N";
    private static final int MAX_REDIRECTS = 10;

    public JsonObject authenticate(String userId, String password) {
        try {
            CookieStore cookieStore = new BasicCookieStore();
            CloseableHttpClient client = login(userId, password, cookieStore);

            if (client != null) {
                System.out.println("✅ 로그인 성공");
                return followRedirects(client, AUTH_AFTER_URL, 0, cookieStore, userId);
            } else {
                System.out.println("❌ 로그인 실패");
                return createErrorResponse(false, "로그인 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(false, e.getMessage());
        }
    }

    private CloseableHttpClient login(String userId, String password, CookieStore cookieStore) throws IOException {
        RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();

        CloseableHttpClient client = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(config)
                .build();

        HttpPost post = new HttpPost(LOGIN_URL);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("User-Agent", "Mozilla/5.0");
        post.setHeader("Referer", RETURN_URL);

        String data = "userId=" + URLEncoder.encode(userId, "UTF-8") +
                "&password=" + URLEncoder.encode(password, "UTF-8") +
                "&returnUrl=" + URLEncoder.encode("https://success.sungkyul.ac.kr/sso/main.aspx", "UTF-8");

        post.setEntity(new StringEntity(data));

        try (CloseableHttpResponse response = client.execute(post)) {
            String body = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == 200 && body.contains("\"result\":\"SUCCESS\"")) {
                return client;
            }
        }
        return null;
    }

    private JsonObject followRedirects(CloseableHttpClient client, String url, int redirectCount, CookieStore cookieStore, String userId) throws IOException {
        if (redirectCount > MAX_REDIRECTS) {
            throw new IOException("🚨 리디렉션 최대 횟수를 초과했습니다.");
        }

        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("User-Agent", "Mozilla/5.0");
        getRequest.setHeader("Referer", RETURN_URL);

        try (CloseableHttpResponse response = client.execute(getRequest)) {
            int statusCode = response.getStatusLine().getStatusCode();

            Header locationHeader = response.getFirstHeader("Location");

            if (statusCode == 302 && locationHeader != null) {
                String redirectUrl = locationHeader.getValue();
                if (!redirectUrl.startsWith("https")) {
                    URI baseUri = URI.create(url);
                    redirectUrl = baseUri.getScheme() + "://" + baseUri.getHost() + redirectUrl;
                }

                System.out.println("➡ 리디렉션 감지");

                if (redirectUrl.contains(RETURN_URL)) {
                    String pageContent = printPageContent(client, redirectUrl);
                    extractAndSetCookiesFromHTML(pageContent, cookieStore);
                    return accessProfileData(client, userId);
                } else {
                    return followRedirects(client, redirectUrl, redirectCount + 1, cookieStore, userId);
                }
            } else if (statusCode == 200) {
                String pageContent = printPageContent(client, url);
                extractAndSetCookiesFromHTML(pageContent, cookieStore);
                return accessProfileData(client, userId);
            } else {
                throw new IOException("⚠️ 리디렉션 중 오류 발생: 응답 코드 " + statusCode);
            }
        }
    }

    private String printPageContent(CloseableHttpClient client, String url) throws IOException {
        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader("User-Agent", "Mozilla/5.0");

        try (CloseableHttpResponse response = client.execute(getRequest)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    private void extractAndSetCookiesFromHTML(String html, CookieStore cookieStore) {
        Pattern pattern = Pattern.compile("document\\.cookie\\s*=\\s*\\\"([A-Za-z0-9_]+)=([A-Za-z0-9]+);");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);

            BasicClientCookie cookie = new BasicClientCookie(name, value);
            cookie.setDomain("success.sungkyul.ac.kr");
            cookie.setPath("/");
            cookieStore.addCookie(cookie);
        }
    }

    private JsonObject accessProfileData(CloseableHttpClient client, String userId) throws IOException {
        HttpGet getRequest = new HttpGet(PROFILE_DATA_URL);
        getRequest.setHeader("User-Agent", "Mozilla/5.0");

        try (CloseableHttpResponse response = client.execute(getRequest)) {
            String profileContent = EntityUtils.toString(response.getEntity());
            return parseProfileData(profileContent, userId);
        }
    }

    private JsonObject parseProfileData(String htmlContent, String userId) {
        JsonObject profileJson = new JsonObject();
        profileJson.addProperty("is_auth", true);

        Pattern studentIdPattern = Pattern.compile("<div>(\\d+)</div>");
        Matcher studentIdMatcher = studentIdPattern.matcher(htmlContent);
        String studentID = studentIdMatcher.find() ? studentIdMatcher.group(1) : null;

        Pattern userNamePattern = Pattern.compile("<div style='font-size:11pt;font-weight:bold'>(.*?)</div>");
        Matcher userNameMatcher = userNamePattern.matcher(htmlContent);
        String userName = userNameMatcher.find() ? userNameMatcher.group(1) : null;

        Pattern majorPattern = Pattern.compile("<div>([^<]*?)</div>", Pattern.DOTALL);
        Matcher majorMatcher = majorPattern.matcher(htmlContent);

        String major = null;
        int count = 0;
        while (majorMatcher.find()) {
            count++;
            if (count == 2) { // 학과
                major = majorMatcher.group(1).trim();
                break;
            }
        }

        JsonObject profileData = new JsonObject();
        profileData.addProperty("studentID", studentID);
        profileData.addProperty("userName", userName);
        profileData.addProperty("major", major);
        profileData.addProperty("portalID", userId);

        profileJson.add("profile", profileData);

        return profileJson;
    }

    private JsonObject createErrorResponse(boolean isAuth, String errorMessage) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("is_auth", isAuth);
        errorJson.addProperty("error", errorMessage);

        return errorJson;
    }
}
