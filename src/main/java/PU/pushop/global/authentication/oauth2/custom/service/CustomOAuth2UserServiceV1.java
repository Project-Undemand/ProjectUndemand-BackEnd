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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserServiceV1 extends DefaultOAuth2UserService {

    private final MemberRepositoryV1 memberRepositoryV1;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2 정보를 가져옵니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("=================== getAttributes() 시작 ================== 개발단계 ====");
        log.info("1.getAttributes : {}", oAuth2User.getAttributes());
        // OAuth2 소셜 로그인 타입에 맞게 response 를 받아옵니다.
        String registrationType = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = createOAuth2Response(registrationType, oAuth2User.getAttributes());
        if (oAuth2Response == null) {
            return null;
        }
        log.info("2.registrationType : {}", registrationType);
        log.info("3.oAuth2Response : {}", oAuth2Response);
        log.info("=================== getAttributes() 끝  ================== 개발단계 ====");
        // 인증 정보에 대해 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("Is authenticated: {}", authentication.isAuthenticated());
        } else {
            log.info("Authentication object is null.");
        }
        /*
          1. email, username, role, socialType, socialId 를 oAuth2Response 로부터 받아서
          2. memberRepository 에 저장하고
          3. Member 객체로 반환
         */
        Member memberByOAuth2Response = getOrCreateMember(oAuth2Response);
        // Member 객체 -> OAuth2User 로 변경
        CustomOAuth2User customOAuth2User = createOAuth2User(memberByOAuth2Response);
        log.info("가입된 CustomOAuth2User 의 getAttributes = " + customOAuth2User.getAttributes().toString());

        return customOAuth2User;
    }

    private OAuth2Response createOAuth2Response(String registrationType, Map<String, Object> attributes) {
        return switch (registrationType) {
            case "naver" -> new NaverResponse(attributes);
            case "google" -> new GoogleResponse(attributes);
            case "kakao" -> new KakaoResponse(attributes);
            default -> null;
        };
    }

    private Member getOrCreateMember(OAuth2Response oAuth2Response) {
        String ProviderId = oAuth2Response.getProviderId();
        // 소셜로그인 - 식별자 socialId
        String SocialId = oAuth2Response.getProvider() + "-" + ProviderId;
        // 소셜로그인 - 타입을 가져옵니다.
        SocialType SocialType = mapRegistrationTypeToSocialType(oAuth2Response.getProvider());
        // getEmail 과 getName 을 통해서 이메일과 실제 사용자명을 받아옵니다.
        String email = oAuth2Response.getEmail();
        String username = oAuth2Response.getName();
        // 식별자 socialId 값으로 멤버를 가져옵니다. 있으면 덮어씌우고, 없으면 새로 생성합니다.
        Optional<Member> OptionalMember = memberRepositoryV1.findBySocialId(SocialId);
        // registrationType 을 SocialType 으로 변환
        if (OptionalMember.isPresent()) {
            // 불 필요한 로직 주석처리 : 똑같은 데이터를 update 하고 있음. [2024.04.08]
//            Member newOAuth2Member = Member.createOAuth2Member(Email, Username, SocialType, SocialId);
//            newOAuth2Member.updateOAuth2Member(newOAuth2Member);
//            memberRepositoryV1.save(newOAuth2Member);
            return OptionalMember.get();
        } else {
            Member newOAuth2Member = Member.createSocialMember(email, username, MemberRole.USER, SocialType, SocialId);
            memberRepositoryV1.save(newOAuth2Member);
            return newOAuth2Member;
        }
    }



    private CustomOAuth2User createOAuth2User(Member member) {

        OAuthUserDTO userDTO = OAuthUserDTO.createOAuthUserDTO(member.getId(), member.getEmail(), member.getUsername(), member.getMemberRole(), member.getSocialType(), member.getSocialId(), true);
        return new CustomOAuth2User(userDTO);
    }

    private SocialType mapRegistrationTypeToSocialType(String registrationType) {
        return switch (registrationType) {
            case "naver" -> SocialType.NAVER;
            case "google" -> SocialType.GOOGLE;
            case "kakao" -> SocialType.KAKAO;
            default -> SocialType.GENERAL;
        };
    }
}
