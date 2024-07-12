package com.SkyGradU.SkyGradU.service;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    public Map<String, Object> authenticate(String userId, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 성결대학교 포털에 로그인 요청
            Document loginResponse = Jsoup.connect("https://www.sungkyul.ac.kr/portalLogin/skukr/loginProcess.do")
                    .data("userId", userId)
                    .data("password", password)
                    .post();

            // 로그인 성공 여부 확인
            if (loginResponse.text().contains("result\":\"SUCCESS\"")) {
                response.put("is_auth", true);

                // 추가 정보 수집
                Document ssoPage = Jsoup.connect("https://sky.sungkyul.ac.kr:444/sso/index.jsp").get();
                String title = ssoPage.title();
                String studentID = title.replaceAll("[^0-9]", "");

              /*  Document mainPage = Jsoup.connect("https://success.sungkyul.ac.kr/Career/startpage.aspx").get();
                String usernameGrade = mainPage.select(".pull-left.text-center").get(0).text();
                String[] userDetails = usernameGrade.split("\\|");
                String username = userDetails[0].trim();
                String grade = userDetails[1].trim();
                String major = mainPage.select(".pull-left.text-center").get(1).text().trim();
                String userEmail = mainPage.select(".pull-left.text-center").get(3).text().trim();*/

                response.put("StudentID", studentID);
                /*response.put("Username", username);
                response.put("Grade", grade);*/
                /*response.put("Major", major);
                response.put("UserEmail", userEmail);
*/
            } else {
                response.put("is_auth", false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.put("is_auth", false);
        }
        return response;
    }
}