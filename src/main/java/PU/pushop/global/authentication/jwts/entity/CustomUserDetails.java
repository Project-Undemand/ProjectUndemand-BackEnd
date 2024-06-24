package PU.pushop.global.authentication.jwts.entity;

import PU.pushop.members.entity.enums.MemberRole;
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

    public Long getMemberId() { return customMemberDto.getMemberId(); }

    public MemberRole getMemberRole(){
        return customMemberDto.getMemberRole();
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
