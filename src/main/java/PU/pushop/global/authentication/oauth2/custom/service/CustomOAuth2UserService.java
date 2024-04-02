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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepositoryV1 memberRepositoryV1;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

//        System.out.println("=================== oAuth2User 출력 ================== 개발단계 ====");
//        System.out.println(oAuth2User);

        String registrationType = userRequest.getClientRegistration().getRegistrationId();
//        System.out.println("registrationType = " + registrationType);
        System.out.println("=================== getAttributes() 시작 ================== 개발단계 ====");
        System.out.println(oAuth2User.getAttributes());
        System.out.println("=================== getAttributes() 끝   ================== 개발단계 ====");
        OAuth2Response oAuth2Response = createOAuth2Response(registrationType, oAuth2User.getAttributes());
//        System.out.println("=================== oAuth2Response 시작 ================== 개발단계 ====");
//        System.out.println(oAuth2Response.getGender());
//        System.out.println("=================== oAuth2Response 끝 ================== 개발단계 ====");
        if (oAuth2Response == null) {
            return null;
        }
        // registrationType을 SocialType으로 매핑
        SocialType socialType = mapRegistrationTypeToSocialType(oAuth2Response.getProvider());
        String socialId = oAuth2Response.getProvider() + "-" + oAuth2Response.getProviderId();

        Member memberEntity = getOrCreateMember(oAuth2Response);

        return createOAuth2User(memberEntity, oAuth2Response, socialType, socialId);
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

    private Member getOrCreateMember(OAuth2Response oAuth2Response) {
        String providerId = oAuth2Response.getProviderId();
        String socialId = oAuth2Response.getProvider() + "-" + providerId;

        Optional<Member> existMember = memberRepositoryV1.findBySocialId(socialId);
        // registrationType 을 SocialType 으로 변환
        SocialType socialType = mapRegistrationTypeToSocialType(oAuth2Response.getProvider());
        if (existMember.isPresent()) {
            Member memberEntity = existMember.get();
            Member newOAuth2Member = Member.createOAuth2Member(oAuth2Response.getEmail(), oAuth2Response.getName(), socialType, socialId);
            memberEntity.updateOAuth2Member(newOAuth2Member);
            memberRepositoryV1.save(memberEntity);
            return memberEntity;
        } else {
            Member memberEntity = Member.createSocialMember(oAuth2Response.getEmail(), oAuth2Response.getName(), MemberRole.USER, socialType, socialId);
            memberRepositoryV1.save(memberEntity);
            return memberEntity;
        }
    }



    private OAuth2User createOAuth2User(Member memberEntity, OAuth2Response oAuth2Response, SocialType socialType, String socialId) {

        OAuthUserDTO userDTO = OAuthUserDTO.createOAuthUserDTO(oAuth2Response.getName(), memberEntity.getMemberRole(), socialType, socialId);
        return new CustomOAuth2User(userDTO);
    }

    private SocialType mapRegistrationTypeToSocialType(String registrationType) {
        switch (registrationType) {
            case "naver":
                return SocialType.NAVER;
            case "google":
                return SocialType.GOOGLE;
            case "kakao":
                return SocialType.KAKAO;
            default:
                return SocialType.GENERAL;
        }
    }
}
