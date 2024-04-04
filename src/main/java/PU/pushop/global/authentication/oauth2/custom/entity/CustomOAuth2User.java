package PU.pushop.global.authentication.oauth2.custom.entity;

import PU.pushop.members.model.OAuthUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuthUserDTO oAuthUserDTO;

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
        // 로그인 이용자의 실제 이름
        return oAuthUserDTO.getUsername();
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    public String getUsername() {
        // 로그인 이용자의 가상 네임
        return oAuthUserDTO.getNickname();
    }

    public String getSocialType() {
        // 로그인 이용자의 가상 네임
        return oAuthUserDTO.getSocialType().toString();
    }

    public String getSocialId() {
        // 로그인 이용자의 가상 네임
        return oAuthUserDTO.getSocialId();
    }


}
