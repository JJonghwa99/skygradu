import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PortalLoginService {

    private static final String LOGIN_URL = "https://www.sungkyul.ac.kr/portalLogin/skukr/portalLoginForm.do";
    private static final String REDIRECT_URL = "https://www.sungkyul.ac.kr/portalLogin/skukr/portalPage.do";

    public boolean authenticate(String userId, String password) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("userId", userId);
            data.put("password", password);

            Connection.Response response = Jsoup.connect(LOGIN_URL)
                    .data(data)
                    .method(Connection.Method.POST)
                    .followRedirects(false) // 자동 리다이렉트 방지
                    .execute();

            return response.header("Location").equals(REDIRECT_URL);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateToken(String userId) {
        // Simple token generation logic (should be replaced with a more secure one)
        return userId + "_token";
    }
}
