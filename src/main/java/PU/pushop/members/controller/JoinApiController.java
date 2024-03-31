package PU.pushop.members.controller;


import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.entity.enums.SocialType;
import PU.pushop.members.service.JoinService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class JoinApiController {

    private final JoinService joinService;

    /**
     * 일반 회원에 대한 회원가입 진행. (default) MemberRole = USER, SocialType = GENERAL
     * @param request email, password, username, nickname
     * @return memberId
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinMemberV1(@RequestBody @Valid JoinMemberRequest request) {
        // password 와 password_certify 검증은 프론트에서 진행, 서버에서 검증하지 않음
//        validatePasswordMatch(request.getPassword(), request.getPassword_certify());

        // request로부터 받은 데이터로 Member 객체 생성.
        Member member = createMemberFromRequest(request);
        // 멤버 객체를 가지고 회원가입 Join 서비스 진행.
        Long memberId = joinService.joinMember(member);
        JoinMemberResponse response = new JoinMemberResponse(memberId);
        // 회원가입 진행한 멤버의 id만 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/admin/join")
    public ResponseEntity<?> joinAdmin(@RequestBody @Valid JoinMemberRequest request) {
//        validatePasswordMatch(request.getPassword(), request.getPassword_certify());

        // ADMIN 을 따로 생성하는 페이지를 따로 구성해서, 진행시킬 예정.
        Member member = createAdminFromRequest(request);
        // 멤버 객체를 가지고 회원가입 Join 서비스 진행.
        Long adminId = joinService.joinMember(member);
        JoinMemberResponse response = new JoinMemberResponse(adminId);
        // 회원가입 진행한 멤버의 id만 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    private void validatePasswordMatch(String password, String passwordCertify) {
//        if (!Objects.equals(password, passwordCertify)) {
//            throw new PasswordMismatchException();
//        }
//    }

    private Member createMemberFromRequest(JoinMemberRequest request) {
        Member member = Member.createGeneralMember(request.email, request.username, request.nickname, request.password);
        return member;
    }

    private Member createAdminFromRequest(JoinMemberRequest request) {
        Member member = Member.createAdminMember(request.email, request.password);
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

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public class PasswordMismatchException extends IllegalArgumentException {
//        public PasswordMismatchException() {
//            super("Password and password confirmation do not match");
//        }
//    }
}
