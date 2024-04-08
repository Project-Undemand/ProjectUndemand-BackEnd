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

        String registrationType = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("=================== getAttributes() 시작 ================== 개발단계 ====");
        System.out.println(oAuth2User.getAttributes());
        System.out.println("=================== getAttributes() 끝  ================== 개발단계 ====");

        OAuth2Response oAuth2Response = createOAuth2Response(registrationType, oAuth2User.getAttributes());
        if (oAuth2Response == null) {

            return null;
        }
        // email, username, role, socialType, socialId 를 oAuth2Response 로부터 받아서 Member 객체로 반환
        Member memberByOAuth2Response = getOrCreateMember(oAuth2Response);
        // Member 객체 -> OAuth2User 로 변경
        OAuth2User CustomOAuth2User = createOAuth2User(memberByOAuth2Response);
        System.out.println("CustomOAuth2User = " + CustomOAuth2User.getAttributes().toString());

        return CustomOAuth2User;
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
        String ProviderId = oAuth2Response.getProviderId();
        // 소셜로그인 - 식별자 socialId
        String SocialId = oAuth2Response.getProvider() + "-" + ProviderId;
        // 소셜로그인 - 타입을 가져옵니다.
        SocialType SocialType = mapRegistrationTypeToSocialType(oAuth2Response.getProvider());
        String Email = oAuth2Response.getEmail();
        String Username = oAuth2Response.getName();
        // 식별자 socialId 값으로 멤버를 가져옵니다. 있으면 덮어씌우고, 없으면 새로 생성합니다.
        Optional<Member> OptionalMember = memberRepositoryV1.findBySocialId(SocialId);
        // registrationType 을 SocialType 으로 변환
        if (OptionalMember.isPresent()) {
            Member existedMember = OptionalMember.get();
            // 불 필요한 로직 주석처리 : 똑같은 데이터를 update 하고 있음. 조회, update 쿼리가 불필요하게 나감.
//            Member newOAuth2Member = Member.createOAuth2Member(Email, Username, SocialType, SocialId);
//            newOAuth2Member.updateOAuth2Member(newOAuth2Member);
//            memberRepositoryV1.save(newOAuth2Member);
            return existedMember;
        } else {
            Member newOAuth2Member = Member.createSocialMember(Email, Username, MemberRole.USER, SocialType, SocialId);
            memberRepositoryV1.save(newOAuth2Member);
            return newOAuth2Member;
        }
    }



    private OAuth2User createOAuth2User(Member member) {

        OAuthUserDTO userDTO = OAuthUserDTO.createOAuthUserDTO(member.getEmail(), member.getUsername(), member.getMemberRole(), member.getSocialType(), member.getSocialId(), true);
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
