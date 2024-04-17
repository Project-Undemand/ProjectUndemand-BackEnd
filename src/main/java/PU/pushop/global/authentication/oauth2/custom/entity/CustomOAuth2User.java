package PU.pushop.global.authentication.oauth2.custom.entity;

import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import PU.pushop.members.model.OAuthUserDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User, Serializable {

    private final OAuthUserDTO oAuthUserDTO;

    // email, username, role, socialType, socialId
    private String email;
    @Getter
    private String username;
    @Getter
    private MemberRole role;
    @Getter
    private SocialType socialType;
    @Getter
    private String socialId;

    public CustomOAuth2User(OAuthUserDTO oAuthUserDTO) {
        this.oAuthUserDTO = oAuthUserDTO;
        this.email = oAuthUserDTO.getEmail();
        this.username = oAuthUserDTO.getUsername();
        this.role = oAuthUserDTO.getRole();
        this.socialType = oAuthUserDTO.getSocialType();
        this.socialId = oAuthUserDTO.getSocialId();
    }

    @Override
    public Map<String, Object> getAttributes() {
//        return null;
        return oAuthUserDTO.toMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return oAuthUserDTO.getRole().toString();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        // 로그인 이용자의 email
        return email;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    public Long getMemberId() { return oAuthUserDTO.getMemberId(); } // [2024.04.17 김성우]

}
