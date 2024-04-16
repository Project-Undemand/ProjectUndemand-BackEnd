package PU.pushop.global.authentication.jwts.service;

import PU.pushop.global.authentication.jwts.customuserlogin.CustomUserDetails;
import PU.pushop.global.authentication.jwts.customuserlogin.dto.CustomMemberDto;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepositoryV1 memberRepositoryV1;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepositoryV1.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));

        CustomMemberDto customMemberDto = CustomMemberDto.createCustomMember(member);

        return new CustomUserDetails(customMemberDto);
    }
}
