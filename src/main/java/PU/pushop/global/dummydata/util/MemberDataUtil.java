package PU.pushop.global.dummydata.util;


import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberDataUtil {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final PasswordEncoder passwordEncoder;

    public void generateMemberData() {
        // 일반 회원
        List<Member> users = List.of(
                createGeneralMember("user1@pushop.com", "pushop", "user1"),
                createGeneralMember("user2@pushop.com", "pushop", "user2"),
                createGeneralMember("user3@pushop.com", "pushop", "user3")
        );

        // 판매자 회원
        List<Member> sellers = List.of(
                createSellerMember("seller1@pushop.com", "pushop", "seller1", "FashionThreads Inc"),
                createSellerMember("seller2@pushop.com", "pushop", "seller2", "Elite Apparel Co"),
                createSellerMember("seller3@pushop.com", "pushop", "seller3", "Stylish Stitches Ltd"),
                createSellerMember("seller4@pushop.com", "pushop", "seller4", "Urban Garments Manufacturing"),
                createSellerMember("seller5@pushop.com", "pushop", "seller5", "ChicWear Productions"),
                createSellerMember("seller6@pushop.com", "pushop", "seller6", "TrendSet Clothing Co"),
                createSellerMember("seller7@pushop.com", "pushop", "seller7", "Elegant Fabrics Ltd")
        );

        // 관리자 회원
        List<Member> admins = List.of(
                createAdminMember("admin1@pushop.com", "pushop", "admin1"),
                createAdminMember("admin2@pushop.com", "pushop", "admin2"),
                createAdminMember("admin3@pushop.com", "pushop", "admin3")
        );

        // 모든 회원 저장
        memberRepositoryV1.saveAll(users);
        memberRepositoryV1.saveAll(sellers);
        memberRepositoryV1.saveAll(admins);
    }

    private Member createGeneralMember(String email, String password, String nickname) {
        // Generate a UUID for socialId
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 표준화된 128-bit의 고유 식별자
        String socialId = "general-" + uuid.substring(0, 12);
        String token = UUID.randomUUID().toString();
        return Member.createUserMember(email, nickname, passwordEncoder.encode(password), token, socialId);
    }

    private Member createSellerMember(String email, String password, String nickname, String manufacturer) {
        // Generate a UUID for socialId
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 표준화된 128-bit의 고유 식별자
        String socialId = "general-" + uuid.substring(0, 12);
        String token = UUID.randomUUID().toString();
        Member member = Member.createSellerMember(email, nickname, passwordEncoder.encode(password), token, manufacturer, socialId);
        return member;
    }

    private Member createAdminMember(String email, String password, String nickname) {
        // Generate a UUID for socialId
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 표준화된 128-bit의 고유 식별자
        String socialId = "general-" + uuid.substring(0, 12);
        String token = UUID.randomUUID().toString();
        return Member.createAdminMember(email, nickname, passwordEncoder.encode(password), token, socialId);
    }
}
