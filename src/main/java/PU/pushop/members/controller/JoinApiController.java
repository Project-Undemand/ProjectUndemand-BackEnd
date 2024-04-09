package PU.pushop.members.controller;


import PU.pushop.global.mail.service.EmailMemberService;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.service.JoinService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class JoinApiController {

    private final JoinService joinService;
    private final EmailMemberService emailMemberService;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 일반 회원에 대한 회원가입 진행. (default) MemberRole = USER, SocialType = GENERAL,
     * 이메일 인증 로직 추가.
     * @param request email, password, username, nickname
     * @return memberId
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinMemberV1(@RequestBody @Valid JoinMemberRequest request) {
        // 표준화된 128-bit의 고유 식별자
        String token = UUID.randomUUID().toString();

        // request로부터 받은 데이터로 Member 객체 생성.
        Member member = createMemberFromRequest(request, token);
        try {
            // 동일한 이메일이 존재하는지 유효성 검사.
            validateExistedMemberByEmail(member.getEmail());
        } catch (JoinService.ExistingMemberException e) {
            // 클라이언트에게 400 Bad Request 오류를 반환.

            return ResponseEntity.badRequest().body(member.getEmail() + " : 이미 등록된 이메일입니다.");
        }
        // 멤버 객체를 가지고 회원가입 Join 서비스 진행.
        Long memberId = joinService.joinMember(member);

        // 회원가입 완료 후 이메일 인증 메일 전송
        sendVerificationEmail(member.getEmail(), token);

        JoinMemberResponse response = new JoinMemberResponse(memberId);
        // 회원가입 진행한 멤버의 id만 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        // queryParameter 로 전해진 token 값에 대한 유효성검사 및 인증과정 진행.
        Member member = emailMemberService.updateByVerifyToken(token);
        if (member != null) {
            return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유효하지 않은 토큰입니다.");
        }
    }

    private void validateExistedMemberByEmail(String email) {
        boolean isExistMember = memberRepositoryV1.existsByEmail(email);
        if (isExistMember) {
            throw new JoinService.ExistingMemberException();
        }
    }

    // 이메일 인증 메일 전송
    private void sendVerificationEmail(String email, String token) {
        try {
            Member member = Member.createEmailMember(email, token); // isCertifyByMail = false
            emailMemberService.add(member);
        } catch (Exception e) {
            // 이메일 전송에 실패한 경우 처리
            e.printStackTrace();
        }
    }

    @PostMapping("/admin/join")
    public ResponseEntity<?> joinAdmin(@RequestBody @Valid JoinMemberRequest request) {
//        validatePasswordMatch(request.getPassword(), request.getPassword_certify());
        // 표준화된 128-bit의 고유 식별자
        String token = UUID.randomUUID().toString();

        // ADMIN 을 따로 생성하는 페이지를 따로 구성해서, 진행시킬 예정.
        Member member = createAdminFromRequest(request, token);
        member.verifyAdminUser();
        // 동일한 이메일이 존재하는지 유효성 검사.
        try {
            validateExistedMemberByEmail(member.getEmail());
        } catch (JoinService.ExistingMemberException e) {
            // 클라이언트에게 400 Bad Request 오류를 반환.
            return ResponseEntity.badRequest().build();
        }
        // 멤버 객체를 가지고 회원가입 Join 서비스 진행.
        Member newAdminMember = memberRepositoryV1.save(member);
        JoinMemberResponse response = new JoinMemberResponse(newAdminMember.getId());
        // 회원가입 진행한 멤버의 id만 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Member createMemberFromRequest(JoinMemberRequest request, String token) {
        Member member = Member.createGeneralMember(
                request.email,
                request.username,
                request.nickname,
                request.password,
                token
        );
        return member;
    }

    private Member createAdminFromRequest(JoinMemberRequest request, String token) {
        Member member = Member.createAdminMember(
                request.email,
                request.username,
                request.nickname,
                passwordEncoder.encode(request.password),
                token
        );
        return member;
    }

    @Data
    static class JoinMemberRequest {
        private String email;
        private String password;
        private String password_certify;
        private String username;
        private String nickname;
        private MemberRole role;
        private SocialType socialType;
    }

    @Data
    private class JoinMemberResponse {
        private Long id;

        public JoinMemberResponse(Long id) {
            this.id = id;
        }
    }

}
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public class PasswordMismatchException extends IllegalArgumentException {
//        public PasswordMismatchException() {
//            super("Password and password confirmation do not match");
//        }
//    }


