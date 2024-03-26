package PU.pushop.members.entity;


import PU.pushop.members.entity.enums.LoginType;
import PU.pushop.members.entity.enums.MemberRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER")
public class Member {

    @Column(name = "MEMBER_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String email;

    private String password;

    private String username;

    private String nickname;

    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "MEMBER_ROLE")
    private MemberRole role;

    @Column(name = "JOINED_AT")
    private String joinedAt;

    @Column(name = "IS_ACTIVE")
    private boolean isActive = true;

    @Column(name = "IS_ADMIN")
    private boolean isAdmin = false;

    @Column(name = "LOGIN_TYPE")
    private LoginType loginType;

    public static Member createNewMember(String email, String username, String nickname, MemberRole role) {
        Member member = new Member();
        member.setEmail(email);
        member.setUsername(username);
        member.setNickname(nickname);
        member.setRole(role);
        return member;
    }

    public static Member createNewMember(String email, String password, String username, String nickname, MemberRole role) {
        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setUsername(username);
        member.setNickname(nickname);
        member.setRole(role);
        return member;
    }

    public static Member createNewMember(String email, String password, MemberRole role) {
        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setRole(role);
        return member;
    }
}
