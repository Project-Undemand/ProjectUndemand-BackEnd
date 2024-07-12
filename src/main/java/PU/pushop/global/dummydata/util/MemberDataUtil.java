package PU.pushop.global.dummydata.util;


import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberDataUtil {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public void generateMemberData() {
        // 일반 회원 생성
        createGeneralMember("user1@pushop.com", "pushop", "user1");
        createGeneralMember("user2@pushop.com", "pushop", "user2");
        createGeneralMember("user3@pushop.com", "pushop", "user3");

        // 판매자 회원 생성
        createSellerMember("seller1@pushop.com", "pushop", "seller1", "FashionThreads Inc");
        createSellerMember("seller2@pushop.com", "pushop", "seller2", "Elite Apparel Co");
        createSellerMember("seller3@pushop.com", "pushop", "seller3", "Stylish Stitches Ltd");
        createSellerMember("seller4@pushop.com", "pushop", "seller4", "Urban Garments Manufacturing");
        createSellerMember("seller5@pushop.com", "pushop", "seller5", "ChicWear Productions");
        createSellerMember("seller6@pushop.com", "pushop", "seller6", "TrendSet Clothing Co");
        createSellerMember("seller7@pushop.com", "pushop", "seller7", "Elegant Fabrics Ltd");

        // 관리자 회원 생성
        createAdminMember("admin1@pushop.com", "pushop", "admin1");
        createAdminMember("admin2@pushop.com", "pushop", "admin2");
        createAdminMember("admin3@pushop.com", "pushop", "admin3");
    }

    private void createGeneralMember(String email, String password, String nickname) {
        String socialId = generateSocialId();
        String token = UUID.randomUUID().toString();
        Member generalMember = Member.createUserMember(email, nickname, passwordEncoder.encode(password), token, socialId);
        saveMemberAndProfile(generalMember);
    }

    private void createSellerMember(String email, String password, String nickname, String manufacturer) {
        String socialId = generateSocialId();
        String token = UUID.randomUUID().toString();
        Member sellerMember = Member.createSellerMember(email, nickname, passwordEncoder.encode(password), token, manufacturer, socialId);
        saveMemberAndProfile(sellerMember);
    }

    private void createAdminMember(String email, String password, String nickname) {
        String socialId = generateSocialId();
        String token = UUID.randomUUID().toString();
        Member adminMember = Member.createAdminMember(email, nickname, passwordEncoder.encode(password), token, socialId);
        saveMemberAndProfile(adminMember);
    }

    private void saveMemberAndProfile(Member member) {
        Member savedMember = memberRepositoryV1.save(member);
        Profiles profile = Profiles.createMemberProfile(savedMember);
        profileRepository.save(profile);
    }

    private String generateSocialId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "general-" + uuid.substring(0, 12);
    }
}
