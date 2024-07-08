package PU.pushop.members.service;

import PU.pushop.global.authentication.jwts.utils.CookieUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.security.auth.login.CredentialNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshRepository refreshRepository;

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

    @Transactional
    public ResponseEntity<?> memberLogout(String refreshAuthorization, HttpServletRequest request, HttpServletResponse response) {
        // refreshAuthorization 쿠키가 null 이거나 비어 있는지 확인
        if (refreshAuthorization == null || refreshAuthorization.isEmpty()) {
            log.warn("로그아웃 요청에 refreshAuthorization 쿠키가 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그아웃 요청에 refreshAuthorization 쿠키가 없습니다.");
        }

        // refreshAuthorization 쿠키가 올바른 형식을 갖추었는지 확인
        if (!refreshAuthorization.startsWith("Bearer+")) {
            log.warn("잘못된 형식의 refreshAuthorization 쿠키입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 형식의 refreshAuthorization 쿠키입니다.");
        }

        String refreshToken = refreshAuthorization.substring(7);

        // refreshToken 이 비어 있는지 확인
        if (refreshToken.isEmpty()) {
            log.warn("refreshToken is Empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("refreshToken is Empty.");
        }

        Optional<Refresh> optionalRefresh = refreshRepository.findByRefreshToken(refreshToken);
        if (optionalRefresh.isPresent()) {
            Refresh refreshEntity = optionalRefresh.get();
            Member member = memberRepositoryV1.findById(refreshEntity.getMember().getId())
                    .orElseThrow(() -> new UsernameNotFoundException("id에 맞는 해당 회원이 존재하지 않습니다."));
            // Response refresh Cookie 삭제
            CookieUtil.deleteCookie(response, "refreshAuthorization");
            // DB 에 있는 refresh 삭제
            refreshRepository.delete(refreshEntity);

            log.info("멤버 Id : " + member.getId() + " 님이 로그아웃 하셨습니다.");
            return ResponseEntity.status(HttpStatus.OK).body("멤버 Id : " + member.getId() + " 님이 로그아웃 하셨습니다.");
        } else {
            log.warn("DB 에 존재하지 않은 잘못된 Refresh token 입니다. 다른 유저의 토큰입니다. Refresh token : " + refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("DB 에 존재하지 않은 잘못된 Refresh token 입니다. 다른 유저의 토큰입니다.");
        }
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

    private void updateLastLoginAt(Member member) {
        member.setLastLoginDate(LocalDateTime.now());
        memberRepositoryV1.save(member);
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
            maskedName.append("*".repeat(length - 2));
            maskedName.append(name.charAt(length - 1));
            return maskedName.toString();
        }
        return name;
    }
}
