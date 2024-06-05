package PU.pushop.global.authentication.jwts.service;

import PU.pushop.global.authentication.jwts.entity.CustomUserDetails;
import PU.pushop.global.authentication.jwts.entity.CustomMemberDto;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberService.validateDuplicatedEmail(email);

        CustomMemberDto customMemberDto = CustomMemberDto.createCustomMember(member);

        return new CustomUserDetails(customMemberDto);
    }
}
