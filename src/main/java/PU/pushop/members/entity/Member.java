package PU.pushop.members.entity;

import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.wishList.entity.WishList;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String email;

    private String password;

    private String username;

    private String nickname;

    private String phone;


    @Enumerated(value = EnumType.STRING)
    @Column(name = "member_role")
    @JsonProperty("member_role")
    private MemberRole memberRole;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type")
    @JsonProperty("social_type")
    private SocialType socialType;

    @Column(name = "social_id")
    @JsonProperty("social_id")
    private String socialId; // Provider + prividerId 형식

    @Column(name = "joined_at")
    @JsonProperty("joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_admin")
    private boolean isAdmin = false;

    @Column(name = "email_token")
    @JsonProperty("email_token")
    private String token;

    @Column(name = "is_certified_email")
    private boolean isCertifyByMail = false;

    @OneToMany(mappedBy = "member")
    private List<WishList> wishLists = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    // 1. 생성자를 통해 멤버 생성
    public Member(String email, String password, String username, String nickname, MemberRole memberRole, SocialType socialType, String socialId, String token, boolean isCertifyByMail) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.memberRole = memberRole;
        this.socialType = socialType;
        this.socialId = socialId;
        this.token = token;
        this.isCertifyByMail = isCertifyByMail;
    }
    // 2. Profile 에 멤버를 담을 때, 민감한 정보(비밀번호, 토큰) 등을 담아주지 않기 위한 생성자
    public Member(String email, String username, String nickname, MemberRole memberRole, SocialType socialType, String socialId, boolean isCertifyByMail) {
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.memberRole = memberRole;
        this.socialType = socialType;
        this.socialId = socialId;
        this.isCertifyByMail = isCertifyByMail;
    }

    // Social Member 생성
    public static Member createSocialMember(String email, String username, MemberRole memberRole, SocialType socialType, String socialId) {
        return new Member(email, null, username, null, memberRole, socialType, socialId, null, true);
    }

    // Email 인증을 진행한 멤버를 생성할때 사용
    public static Member createEmailMember(String email, String token) {
        return new Member(email, null, null, null, MemberRole.USER, SocialType.GENERAL, null, token, false);
    }

    // General Member 생성
    public static Member createGeneralMember(String email, String nickname, String password, String token) {
        return new Member(email, password, null, nickname, MemberRole.USER, SocialType.GENERAL, null, token, false);
    }

    // Admin Member 생성
    public static Member createAdminMember(String email, String nickname, String password, String token) {
        return new Member(email, password, null, nickname, MemberRole.ADMIN, SocialType.GENERAL, null, token, true);
    }

    public static Member createTokenMember(Long memberId, MemberRole role) {
        return null;
    }

    public static Member createProfileMember(Member member) {
        return new Member(member.getEmail(), member.getUsername(), member.getNickname(), member.getMemberRole(), member.getSocialType(), member.getSocialId(), member.isCertifyByMail);
    }


    // 새로운 멤버 객체 생성하여 반환하는 메서드
    public void updateOAuth2Member(Member newOAuth2Member) {
        this.email = newOAuth2Member.getEmail();
        this.username = newOAuth2Member.getUsername();
        this.memberRole = newOAuth2Member.getMemberRole();
        this.socialType = newOAuth2Member.getSocialType();
        this.socialId = newOAuth2Member.getSocialId();
        this.isCertifyByMail = newOAuth2Member.isCertifyByMail();
        // 필요한 경우에 따라 다른 필드도 업데이트할 수 있습니다.
    }

    public static Member createOAuth2Member(String email, String username, SocialType socialType, String socialId) {
        return new Member(email, null, username, null, MemberRole.USER, socialType, socialId, null, true);
    }

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.memberRole = MemberRole.USER;
    }

    // 유저 권한 설정 메소드
    public void authorizeAdmin() {
        this.memberRole = MemberRole.ADMIN;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    // 이메일 인증 여부를 업데이트하는 메서드
    public void certifyByEmail() {
        this.isCertifyByMail = true;
    }

    public void updateMemberByToken(String token) {
        this.token = token;
    }

    public void verifyAdminUser() {
        this.isAdmin = true;
    }

    public void deActivateMember() {
        this.isActive = false;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void activateMember() {
        this.isActive = true;
    }

    public void reSetPassword(String newPassword) {
        this.password = newPassword;
    }
}
