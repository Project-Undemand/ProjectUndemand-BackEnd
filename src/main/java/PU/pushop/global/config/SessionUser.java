package PU.pushop.global.config;

import PU.pushop.members.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class SessionUser implements Serializable {
    private static final long serialVersionUID = -6033498746532854616L;

    private Long userIdNo;
    private String name;

    public SessionUser(Member member, Long cartId) {
        this.userIdNo = member.getId();
        this.name = member.getUsername();
    }
}