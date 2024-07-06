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
import java.time.LocalDateTime;
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
    public Member memberLogin(String email, String password) throws CredentialNotFoundException {
        Member member = findUniqueMemberByEmail(email);

        if (passwordEncoder.matches(password, member.getPassword())) {
            updateLastLoginAt(member);
            return member;
        } else {
            throw new CredentialNotFoundException("Invalid password");
        }
    }

    private void updateLastLoginAt(Member member) {
        member.setLastLoginDate(LocalDateTime.now());
        memberRepositoryV1.save(member);
    }

    @Transactional
    public Member findUniqueMemberByEmail(String email) {
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

    public String maskName(String name) {
        int length = name.length();
        if (length == 2) {
            return name.charAt(0) + "*";
        } else if (length == 3) {
            return name.charAt(0) + "*" + name.charAt(2);
        } else if (length >= 4) {
            StringBuilder maskedName = new StringBuilder();
            maskedName.append(name.charAt(0));
            for (int i = 1; i < length - 1; i++) {
                maskedName.append("*");
            }
            maskedName.append(name.charAt(length - 1));
            return maskedName.toString();
        }
        return name;
    }
}
