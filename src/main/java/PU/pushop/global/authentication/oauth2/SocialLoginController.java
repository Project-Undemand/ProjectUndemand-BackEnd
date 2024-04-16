package PU.pushop.global.authentication.oauth2;

import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.global.authentication.oauth2.custom.dto.KakaoTokenDto;
import PU.pushop.global.authentication.oauth2.custom.service.AuthService;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;


/**
 * This class handles social login operations.
 */
@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final JWTUtil jwtUtil;
    private final OAuth2ClientProperties oauth2Properties;
    private final RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
//    private String naverClientId;
//
//    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
//    private String naverClientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.naver.token-uri}")
//    private String naverTokenUri;
//
//    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
//    private String kakaoClientId;
//
//    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
//    private String kakaoClientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.kakao.token-uri}")
//    private String kakaoTokenUri;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String googleClientId;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String googleClientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.google.token-uri}")
//    private String googleTokenUri;

    private OAuthClientDetails getClientDetails(String registrationId) {
        OAuth2ClientProperties.Registration registration = oauth2Properties.getRegistration().get(registrationId);
        OAuthClientDetails clientDetails = new OAuthClientDetails();
        clientDetails.setClientId(registration.getClientId());
        clientDetails.setClientSecret(registration.getClientSecret());
        clientDetails.setTokenUri(registration.getProvider());
        return clientDetails;
    }

//    @GetMapping("/login/oauth2/callback/kakao")
//    public String handleCallback(@RequestParam("code") String authorizationCode) {
//        try {
//            OAuthClientDetails kakaoDetails = getClientDetails("kakao");
//            OAuthClientDetails googleDetails = getClientDetails("google");
//            OAuthClientDetails naverDetails = getClientDetails("naver");
//
//            // 액세스 토큰 요청을 위한 데이터
//
//            Map<String, String> tokenRequest = new HashMap<>();
//            tokenRequest.put("code", authorizationCode);
//            tokenRequest
//            tokenRequest.put("client_id", kakaoDetails.getClientId());
//            tokenRequest.put("client_secret", kakaoDetails.getClientSecret());
//            tokenRequest.put("redirect_uri", "http://localhost:8080/callback/kakao");
//
//// 액세스 토큰 요청
//            TokenResponse response = restTemplate.postForObject(kakaoDetails.getTokenUri(), tokenRequest, TokenResponse.class);
//            String kakaoClientId = kakaoRegistration.getClientId();
//            String kakaoClientSecret = kakaoRegistration.getClientSecret();
//            String kakaoTokenUri = kakaoRegistration.getProvider();
//
//            String googleClientId = googleRegistration.getClientId();
//            String googleClientSecret = googleRegistration.getClientSecret();
//            String googleTokenUri = googleRegistration.getProvider();
//
//            // 액세스 토큰 요청을 위한 데이터
//            TokenRequest tokenRequest = new TokenRequest(authorizationCode, kakaoClientId, kakaoClientSecret, "http://localhost:8080/callback/kakao");
//            // 액세스 토큰 요청
//            TokenResponse response = restTemplate.postForObject(kakaoTokenUri, tokenRequest, TokenResponse.class);
//            // 액세스 토큰을 클라이언트에게 반환
//            return response.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to fetch access token";
//        }
//    }
    @Data
    public class OAuthClientDetails {
        private String clientId;
        private String clientSecret;
        private String tokenUri;
        // getters and setters
    }
}
