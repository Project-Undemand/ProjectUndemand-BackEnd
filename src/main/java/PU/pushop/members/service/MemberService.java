package PU.pushop.members.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.model.LoginRequest;
import PU.pushop.members.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Member joinMember(Member member) {
        Member newMember = Member.createGeneralMember(
                member.getEmail(),
                member.getNickname(),
                member.getPassword(),
                member.getToken(),
                member.getSocialId()
        );

        newMember.activateMember();

        return memberRepositoryV1.save(newMember);
    }

    @Transactional
    public Member memberLogin(LoginRequest loginRequest) throws UserPrincipalNotFoundException, CredentialNotFoundException {
        List<Member> members = memberRepositoryV1.findAllByEmail(loginRequest.getEmail());

        if (members.size() > 1) {
            log.info("Multiple users found with email:" + loginRequest.getEmail());
            throw new IllegalStateException("Multiple users found with email:" + loginRequest.getEmail());
        } else if (members.size() == 1) {
            Member member = members.get(0);
            if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
                return member;
            } else {
                log.info("Invalid password.");
                throw new CredentialNotFoundException("Invalid password");
            }
        } else {
            log.info("User not found with email: " + loginRequest.getEmail());
            throw new UserPrincipalNotFoundException("User not found with email: " + loginRequest.getEmail());
        }
    }

    @Transactional
    public Member validateDuplicatedEmail(String email) {
        List<Member> members = memberRepositoryV1.findAllByEmail(email);

        if (members.size() > 1) {
            throw new MultipleUsersFoundException("There are multiple users associated with this email: " + email);
        } else if (members.size() == 1) {
            return members.get(0);
        } else {
            throw new UserNotFoundByEmailException("No user found with this email: " + email);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ExistingMemberException extends IllegalStateException {
        public ExistingMemberException() {
            super("이미 존재하는 회원입니다.");
        }
    }

    public static class MultipleUsersFoundException extends RuntimeException {
        public MultipleUsersFoundException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundByEmailException extends RuntimeException {
        public UserNotFoundByEmailException(String message) {
            super(message);
        }
    }
}
