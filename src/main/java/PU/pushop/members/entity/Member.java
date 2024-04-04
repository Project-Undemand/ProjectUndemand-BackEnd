package PU.pushop.members.entity;

import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER")
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String email;

    private String password;

    private String username;

    private String nickname;

    private String phone;

    @Column(name = "JOINED_AT")
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "IS_ACTIVE")
    private boolean isActive = true;

    @Column(name = "IS_ADMIN")
    private boolean isAdmin = false;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "MEMBER_ROLE")
    private MemberRole memberRole;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "SOCIAL_TYPE")
    private SocialType socialType;

    @Column(name = "SOCIAL_ID")
    private String socialId;
    // Provider + prividerId 형식

    // 생성자를 통해 멤버 생성
    public Member(String email, String password, String username, String nickname, MemberRole memberRole, SocialType socialType, String socialId) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.memberRole = memberRole;
        this.socialType = socialType;
        this.socialId = socialId;
    }

    // Social Member 생성
    public static Member createSocialMember(String email, String username, MemberRole memberRole, SocialType socialType, String socialId) {
        return new Member(email, null, username, null, memberRole, socialType, socialId);
    }

    // General Member 생성
    public static Member createGeneralMember(String email, String username, String nickname, String password) {
        return new Member(email, password, username, nickname, MemberRole.USER, SocialType.GENERAL, null);
    }

    // Admin Member 생성
    public static Member createAdminMember(String email, String username, String nickname, String password) {
        return new Member(email, password, username, nickname, MemberRole.ADMIN, SocialType.GENERAL, null);
    }

    // Token Member 생성
    public static Member createTokenMember(String username, MemberRole memberRole) {
        return new Member(username, null, null, null, memberRole, SocialType.GENERAL, null);
    }

    // 새로운 멤버 객체 생성하여 반환하는 메서드
    public void updateOAuth2Member(Member newOAuth2Member) {
        this.email = newOAuth2Member.getEmail();
        this.username = newOAuth2Member.getUsername();
        this.socialType = newOAuth2Member.getSocialType();
        this.socialId = newOAuth2Member.getSocialId();
        // 필요한 경우에 따라 다른 필드도 업데이트할 수 있습니다.
    }

    public static Member createOAuth2Member(String email, String name, SocialType socialType, String socialId) {
        return new Member(email, null, name, null, MemberRole.USER, socialType, socialId);
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
}
