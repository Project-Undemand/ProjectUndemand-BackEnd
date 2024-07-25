package PU.pushop.global.authentication.jwts.service;

import PU.pushop.global.authentication.jwts.entity.CustomUserDetails;
import PU.pushop.global.authentication.jwts.entity.CustomMemberDto;
import PU.pushop.members.entity.Member;
import PU.pushop.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberService.findUniqueMemberByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("No user found with this email: " + email);
        }

        CustomMemberDto customMemberDto = CustomMemberDto.createCustomMember(member);

        return new CustomUserDetails(customMemberDto);
    }
}
