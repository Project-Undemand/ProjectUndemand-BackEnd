package PU.pushop.global.authentication.jwts.entity;


import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import lombok.Data;

@Data
public class CustomMemberDto {

    private Long memberId;

    private String email;

    private String username;

    private String password;

    private MemberRole memberRole;

    private boolean isActive;

    public CustomMemberDto(Long memberId, String email, String username, String password, MemberRole memberRole, boolean isActive) {
        this.memberId = memberId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.memberRole = memberRole;
        this.isActive = isActive;
    }

    public static CustomMemberDto createCustomMember(Member member) {
        return new CustomMemberDto(member.getId(), member.getEmail(), member.getUsername(), member.getPassword(), member.getMemberRole(), member.getIsActive());
    }

    public static CustomMemberDto createCustomMember(Long memberId, MemberRole role, boolean isActive) {
        return new CustomMemberDto(memberId, null, null, null, role, isActive);
    }
}
