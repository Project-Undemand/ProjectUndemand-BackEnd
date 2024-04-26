package PU.pushop.members.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.model.LoginRequest;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.profile.MemberProfile;
import PU.pushop.profile.ProfileRepository;
import PU.pushop.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final ProfileService profileService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Member joinMember(Member member) {

        Member newMember = Member.createGeneralMember(
                member.getEmail(),
                member.getNickname(),
                member.getPassword(),
                member.getToken()
        );

        return memberRepositoryV1.save(newMember);

    }


    @Transactional
    public Member memberLogin(LoginRequest loginRequest) throws UserPrincipalNotFoundException, CredentialNotFoundException {
        Optional<Member> optionalMember = memberRepositoryV1.findByEmail(loginRequest.getEmail());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
                return member;
            } else {
                // 비밀번호가 일치하지 않을 경우 처리
                log.info("Invalid password.");
                throw new CredentialNotFoundException("Invalid password");
            }
        } else {
            // 해당 이메일로 등록된 회원이 없을 경우 처리
            log.info("User not found with email: " + loginRequest.getEmail());
            throw new UserPrincipalNotFoundException("User not found with email: " + loginRequest.getEmail());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ExistingMemberException extends IllegalStateException {
        public ExistingMemberException() {
            super("이미 존재하는 회원입니다.");
        }
    }

}
