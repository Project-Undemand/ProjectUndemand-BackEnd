package PU.pushop.global.authentication.oauth2;

import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.enums.MemberRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class SocialLoginController {

    private final JWTUtil jwtUtil;

    @PostMapping("/chaeyami/hi")
    public ResponseEntity<?> joinMemberV1(HttpServletRequest request, HttpServletResponse response) {
        // request Header 에 있는 Authorization 을 들고옵니다.
        String tokenStr = request.getHeader("Authorization");
        String accessToken = tokenStr.substring(7); // 8번째 부터 읽어옴 (엑세스토큰)
        String memberId = jwtUtil.getMemberId(accessToken);
        MemberRole role = jwtUtil.getRole(accessToken);

        // 회원가입 진행한 멤버의 id만 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
