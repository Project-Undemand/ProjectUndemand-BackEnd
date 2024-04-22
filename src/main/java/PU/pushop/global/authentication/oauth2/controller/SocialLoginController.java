package PU.pushop.global.authentication.oauth2.controller;

import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.global.authentication.oauth2.custom.dto.KakaoProfile;
import PU.pushop.global.authentication.oauth2.custom.dto.OAuthToken;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import PU.pushop.members.model.RefreshDto;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import PU.pushop.members.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;


/**
 * This class handles social login operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final RefreshRepository refreshRepository;
    private final OAuth2ClientProperties oauth2Properties;
    private final JWTUtil jwtUtil;
    // clientDetails
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    private Long refreshTokenExpirationPeriod = 1209600L;

    @GetMapping("/login/oauth2/code/kakao")
    public @ResponseBody void kakaoCallback(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        MultiValueMap<String, String> tokenRequestParams = new LinkedMultiValueMap<>();

        tokenRequestParams.add("grant_type", "authorization_code");
        tokenRequestParams.add("client_id", kakaoClientId);
        tokenRequestParams.add("redirect_uri", kakaoRedirectUri);
        tokenRequestParams.add("code", code);
        tokenRequestParams.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(tokenRequestParams, headers);

        // Http 요청하기 - POST . response 의 응답을 받는다.
        ResponseEntity<String> kakaoTokenResponse = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        /**
         * kakaoResponse.getBody() 의 json 응답 형태 -> OAuthToken Dto 클래스에 담아준다.
         *  {
                "access_token":"KDvqAebC_44Voqc2_9HYg5CRDvWGgABO9EcKPXUaAAABjuxkCHSi-pushoppingmall",
                "token_type":"bearer",
                "refresh_token":"Q9bBxT3_RV7ifGqAapjuwrd1iO_Lv6bJLb8KPXUaAAABjuxkCG-i-pushoppingmall",
                "expires_in":21599,
                "scope":"account_email profile_nickname",
                "refresh_token_expires_in":5183999
         }
        */
        // ObjectMapper 에 kakaoResponse.getBody() 를 담아준다.
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(kakaoTokenResponse.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("oAuthToken.getAccess_token() : " + oAuthToken.getAccess_token());

        RestTemplate restTemplate2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        headers2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);

        // Http 요청하기 - POST . response 의 응답을 받는다.
        ResponseEntity<String> kakaoProfileResponse = restTemplate2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        /** [kakaoProfileResponse.getBody()]
         * {
         *    "id":3416610307,
         *    "connected_at":"2024-04-03T05:39:45Z",
         *    "properties": {
         *       "nickname":"pushoppingmall"
         *    },
         *    "kakao_account":{
         *       "profile_nickname_needs_agreement":false,
         *       "profile":{
         *          "nickname":"pushoppingmall",
         *          "is_default_nickname":false
         *       },
         *       "has_email":true,
         *       "email_needs_agreement":false,
         *       "is_email_valid":true,
         *       "is_email_verified":true,
         *       "email":"pushoppingmall@kakao.com"
         *    }
         * }
         */
        // ObjectMapper 에 kakaoProfileResponse.getBody() 를 담아준다.
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper2.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String email = kakaoProfile.getKakaoAccount().getEmail();
        String username = kakaoProfile.getProperties().getNickname();
        String socialId = "kakao-" + kakaoProfile.getId();
        // socialId 식별자로 중복 회원을 검사한다. 일반 이메일 회원과 소셜 로그인 회원의 이메일이 중복될 수 있기 때문이다.
        Optional<Member> memberWithSocialId = memberRepositoryV1.findBySocialId(socialId);
        if (memberWithSocialId.isPresent()) {
            Member existedMember = memberWithSocialId.get();
            memberRepositoryV1.save(existedMember);
            // response.data에 토큰과 이메일을 넣어준다.
            String jwtAccessToken = jwtUtil.createAccessToken("access", existedMember.getId().toString(), MemberRole.USER.toString());
            String jwtRefreshToken = jwtUtil.createRefreshToken("refresh", existedMember.getId().toString(), MemberRole.USER.toString());
            addResponseData(response, jwtAccessToken, jwtRefreshToken, email);
            saveRefresh(existedMember, jwtRefreshToken);
        } else {
            Member newMember = Member.createSocialMember(email, username, MemberRole.USER, SocialType.KAKAO, socialId);
            memberRepositoryV1.save(newMember);
            // response.data에 토큰과 이메일을 넣어준다.
            String jwtAccessToken = jwtUtil.createAccessToken("access", newMember.getId().toString(), MemberRole.USER.toString());
            String jwtRefreshToken = jwtUtil.createRefreshToken("refresh", newMember.getId().toString(), MemberRole.USER.toString());
            addResponseData(response, jwtAccessToken, jwtRefreshToken, email);
            saveRefresh(newMember, jwtRefreshToken);
        }
        // [response.data] 에 Json 형태로 accessToken 과 refreshToken 을 넣어주는 방식
    }

    /**
     * [response.data] 에 Json 형태로 accessToken 과 refreshToken 을 넣어주는 방식
     * email도 [response.data] 에 추가하였음.
     * 목적 : 로그인 성공 시, 클라이언트에 메세지(환영)를 띄워주기위해.
     */
    private void addResponseData(HttpServletResponse response, String accessToken, String refreshToken, String email) throws IOException {
        // 액세스 토큰을 JsonObject 형식으로 응답 데이터에 포함하여 클라이언트에게 반환
        com.google.gson.JsonObject responseData = new JsonObject();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // response.data 에 accessToken, refreshToken 두값 설정
        responseData.addProperty("accessToken", accessToken);
        responseData.addProperty("refreshToken", refreshToken);
        responseData.addProperty("email", email);
        response.getWriter().write(responseData.toString());
        // HttpStatus 200 OK
        response.setStatus(HttpStatus.OK.value());
    }

    private void saveRefresh(Member member, String newRefreshToken) {
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
            refreshRepository.save(refreshEntity);
        } else {
            // 완전히 새로운 리프레시 토큰을 생성 후 저장
            Refresh newRefreshEntity = new Refresh(member, newRefreshToken, expirationDateTime);
            refreshRepository.save(newRefreshEntity);
        }

    }

    private OAuthClientDetails getClientDetails(String registrationId) {
        OAuth2ClientProperties.Registration registration = oauth2Properties.getRegistration().get(registrationId);
        OAuthClientDetails clientDetails = new OAuthClientDetails();
        clientDetails.setClientId(registration.getClientId());
        clientDetails.setClientSecret(registration.getClientSecret());
        clientDetails.setTokenUri(registration.getProvider());
        return clientDetails;
    }

    @Data
    public class OAuthClientDetails {
        private String clientId;
        private String clientSecret;
        private String tokenUri;
        // getters and setters
    }


}
