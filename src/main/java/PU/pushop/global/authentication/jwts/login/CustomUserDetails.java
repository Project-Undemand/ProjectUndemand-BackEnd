package PU.pushop.global.authentication.jwts.login;

import PU.pushop.global.authentication.jwts.login.dto.CustomMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final CustomMemberDto customMemberDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> customMemberDto.getMemberRole().toString());
        return collection;
    }

    @Override
    public String getPassword() {
        return customMemberDto.getPassword();
    }

    @Override
    public String getUsername() {
        return customMemberDto.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return customMemberDto.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return customMemberDto.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return customMemberDto.isActive();
    }

    @Override
    public boolean isEnabled() {
        return customMemberDto.isActive();
    }


}
