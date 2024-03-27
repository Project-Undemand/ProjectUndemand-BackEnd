package PU.pushop.global.authentication.oauth2.custom.service;

import PU.pushop.global.authentication.oauth2.custom.entity.CustomOAuth2User;
import PU.pushop.global.authentication.oauth2.responsesdto.GoogleResponse;
import PU.pushop.global.authentication.oauth2.responsesdto.KakaoResponse;
import PU.pushop.global.authentication.oauth2.responsesdto.NaverResponse;
import PU.pushop.global.authentication.oauth2.responsesdto.OAuth2Response;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import PU.pushop.members.model.OAuthUserDTO;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepositoryV1 memberRepositoryV1;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println("=================== oAuth2User 출력 ================== 개발단계 ====");
        System.out.println(oAuth2User);

        String registrationType = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = createOAuth2Response(registrationType, oAuth2User.getAttributes());
        if (oAuth2Response == null) {
            return null;
        }
        // OAuth2 로그인 시, 닉네임을 임의로 정해줌 ex) naver + " " + 1209381094832034
        String nickname = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Member memberEntity = getOrCreateMember(oAuth2Response, nickname);

        return createOAuth2User(memberEntity, oAuth2Response);
    }

    private OAuth2Response createOAuth2Response(String registrationType, Map<String, Object> attributes) {
        switch (registrationType) {
            case "naver":
                return new NaverResponse(attributes);
            case "google":
                return new GoogleResponse(attributes);
            case "kakao":
                return new KakaoResponse(attributes);
            default:
                return null;
        }
    }

    private Member getOrCreateMember(OAuth2Response oAuth2Response, String nickname) {
        Member existMember = memberRepositoryV1.findByNickname(nickname);
        if (existMember == null) {
            Member memberEntity = Member.createNewMember(oAuth2Response.getEmail(), oAuth2Response.getName(), nickname, MemberRole.USER);
            memberRepositoryV1.save(memberEntity);
            return memberEntity;
        } else {
            existMember.setEmail(oAuth2Response.getEmail());
            existMember.setUsername(oAuth2Response.getName());
            memberRepositoryV1.save(existMember);
            return existMember;
        }
    }

    private OAuth2User createOAuth2User(Member memberEntity, OAuth2Response oAuth2Response) {
        OAuthUserDTO userDTO = OAuthUserDTO.createOAuthUserDTO(oAuth2Response.getName(), memberEntity.getSocialType());
        return new CustomOAuth2User(userDTO);
    }


}
