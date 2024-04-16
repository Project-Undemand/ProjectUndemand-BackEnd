package PU.pushop.global.authentication.oauth2.custom.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserServiceV2 extends DefaultOAuth2UserService {

    private final ObjectMapper objectMapper;
    @Override

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            log.info(objectMapper.writeValueAsString(oAuth2User.getAttributes()));
        } catch (JsonProcessingException e) {
            log.info("oAuth2User.getAttributes() : objectMapper.writeValueAsString 통한 파싱 실패");
            throw new RuntimeException(e);
        }
        return oAuth2User;
    }
}
