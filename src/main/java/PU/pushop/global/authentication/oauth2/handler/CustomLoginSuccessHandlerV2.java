package PU.pushop.global.authentication.oauth2.handler;


import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.global.authentication.oauth2.custom.entity.CustomOAuth2User;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.model.RefreshDto;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLoginSuccessHandlerV2 extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final RefreshRepository refreshRepository;

    private Long refreshTokenExpirationPeriod = 1209600L;

    @Value("${frontend.url}")
    private String FRONTEND_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getName();
        String role = extractOAuthRole(authentication);
        log.info("=============소셜 로그인 성공, 유저 데이터 시작 ==============");
        log.info("email = " + email);
        log.info("role = " + role);
        log.info("=============소셜 로그인 성공, 유저 데이터 시작 ==============");
        log.info("============= memberId 를 가져오기 위해, DB 조회 시작 ==============");
        Member requestMember = memberRepositoryV1.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));
        log.info("============= memberId 를 가져오기 위해, DB 조회 끝 ==============");
        log.info("requestMember = " + requestMember);
        // 액세스 토큰을 생성합니다.
        String newAccess = jwtUtil.createAccessToken("access", String.valueOf(requestMember.getId()), role);
        // 리프레시 토큰을 생성합니다.
        String newRefresh = jwtUtil.createRefreshToken("refresh", String.valueOf(requestMember.getId()), role);
        log.info("newAccess : " + newAccess);
        log.info("newRefresh : " + newRefresh);

        // [Refresh 토큰 - DB에서 관리합니다.] 리프레쉬 토큰 관리권한이 서버에 있습니다.
        saveOrUpdateRefreshEntity(requestMember, newRefresh);

        // [response.data] 에 Json 형태로 accessToken 과 refreshToken 을 넣어주는 방식
        setTokenResponseV2(response, newAccess, newRefresh);

        response.sendRedirect(FRONTEND_URL);
    }

    private static String extractOAuthRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        return role;
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void saveOrUpdateRefreshEntity(Member member, String newRefreshToken) {
        // 멤버의 PK 식별자로, refresh 토큰을 가져옵니다.
        Optional<Refresh> existedRefresh = refreshRepository.findById(member.getId());
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(refreshTokenExpirationPeriod);
        if (existedRefresh.isPresent()) {
            // 로그인 이메일과 같은 이메일을 가지고 있는 Refresh 엔티티에 대해서, refresh 값을 새롭게 업데이트해줌
            Refresh refreshEntity = existedRefresh.get();
            // Dto 를 통해서, 새롭게 생성한 RefreshToken 값, 유효기간 등을 받아줍니다.
            RefreshDto refreshDto = RefreshDto.createRefreshDto(newRefreshToken, expirationDateTime);
            // Dto 정보들로 기존에 있던 Refresh 엔티티를 업데이트합니다.
            refreshEntity.updateRefreshToken(refreshDto);
            // 저장합니다.
            refreshRepository.save(refreshEntity);
        } else {
            // 완전히 새로운 리프레시 토큰을 생성 후 저장
            Refresh newRefreshEntity = new Refresh(member, newRefreshToken, expirationDateTime);
            refreshRepository.save(newRefreshEntity);
        }

    }

    private void setTokenResponseV1(HttpServletResponse response, String accessToken, String refreshToken) {
        // [reponse Header] : Access Token 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        // [reponse Cookie] : Refresh Token 추가
        response.addCookie(createCookie("refreshToken", refreshToken));
        // HttpStatus 200 OK
        response.setStatus(HttpStatus.OK.value());
    }

    private void setTokenResponseV2(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        // 액세스 토큰을 JSON 형식으로 응답 데이터에 포함하여 클라이언트에게 반환
        JsonObject responseData = new JsonObject();
        responseData.addProperty("accessToken", accessToken);
        responseData.addProperty("refreshToken", refreshToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseData.toString());
        // HttpStatus 200 OK
        response.setStatus(HttpStatus.OK.value());
        // 클라이언트 콘솔에 응답 로그 출력
        log.info("Response sent to client: " + responseData.toString());
    }
}
