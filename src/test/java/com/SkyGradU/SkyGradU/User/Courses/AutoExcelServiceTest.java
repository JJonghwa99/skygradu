package com.SkyGradU.SkyGradU.User.Courses;

import com.SkyGradU.SkyGradU.User.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AutoExcelServiceTest {

    @InjectMocks
    private AutoExcelService autoExcelService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("잘못된 아이디/비밀번호 입력 시 로그인 실패 처리가 수행되는지 검증")
    void testAutoExcelUpdateWithInvalidCredentials() {
        // Given
        String invalidUserId = "wrong_id";
        String invalidPassword = "wrong_password";

        // When
        // 실제 성결대 포털로 로그인 요청이 전송되고 로그인 실패 응답을 수신하여 
        // WebDriver 가 가동되지 않거나 로그인 예외 분기 처리됨
        Map<String, Object> result = autoExcelService.autoExcelUpdate(invalidUserId, invalidPassword);

        // Then
        assertNotNull(result);
        // 로그인 결과 성공이 아니므로 status 는 'fail' 또는 error 정보가 담겨 있어야 함
        assertTrue(result.containsKey("status") || result.containsKey("error") || result.isEmpty());
        if (result.containsKey("status")) {
            assertEquals("fail", result.get("status"));
        }
    }
}
