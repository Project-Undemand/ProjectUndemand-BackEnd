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
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "IS_ACTIVE")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "IS_ADMIN")
    @Builder.Default
    private boolean isAdmin = false;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "LOGIN_TYPE")
    private MemberRole memberRole;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "SOCIAL_TYPE")
    private SocialType socialType;

    public static Member createNewMember(String email, String username, String nickname, MemberRole memberRole) {
        Member member = new Member();
        member.setEmail(email);
        member.setUsername(username);
        member.setNickname(nickname);
        member.setMemberRole(memberRole);
        return member;
    }

    public static Member createNewMember(String email, String password, String username, String nickname,  MemberRole memberRole) {
        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setUsername(username);
        member.setNickname(nickname);
        member.setMemberRole(memberRole);
        return member;
    }

    public static Member createNewMember(String email, String password, MemberRole memberRole) {
        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setMemberRole(memberRole);
        return member;
    }

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.memberRole = MemberRole.USER;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
