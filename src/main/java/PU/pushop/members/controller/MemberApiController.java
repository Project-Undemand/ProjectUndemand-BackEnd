package PU.pushop.members.controller;

import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.global.authorization.MemberAuthorizationUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.repository.MemberRepositoryV1;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final JWTUtil jwtUtil;

    /**
     * 관리자 페이지에서 회원에 대한 모든 데이터를 확인할 때 사용하는 목적. 서비스 목적이 아닙니다.
     * ADMIN 관리자가 아니면 실행할 수 없는 api
     */
    @GetMapping("/")
    public ResponseEntity<?> getMemberList(HttpServletRequest request) {
        // Request Header 에 담아준 Authorization 을 가져와서
        String authorization = request.getHeader("Authorization");
        // accessToken 을 꺼내주는 방식
        String accessToken = authorization.substring(7);
        // accessToken 에 있는 유저 권한을 파싱해서 가져옴
        MemberRole userRole = jwtUtil.getRole(accessToken);

        // ADMIN 관리자가 아니면 실행할 수 없는 api
        if (userRole == MemberRole.ADMIN) {
            // memberId에 해당하는 Member 정보를 조회합니다.
            List<Member> allMembers = memberRepositoryV1.findAll();

            // 회원을 찾은 경우 200 OK 응답과 함께 Member 정보를 반환합니다.
            return ResponseEntity.ok(allMembers);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자페이지에서, 관리자 권한으로만 회원 전체에 대한 조회가 가능합니다.!");
        }
    }

    /**
     * 회원 비활성화
     */
    @PostMapping("/deactive/{memberId}")
    public ResponseEntity<?> deactiveMember(@PathVariable Long memberId, HttpServletRequest request) {
        // 회원 비활성화 조건 : 접속 유저 id == request.user.getId()
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        // memberId 로 회원 조회
        Optional<Member> optionalMember = memberRepositoryV1.findById(memberId);
        if (optionalMember.isPresent()) {
            Member existingMember = optionalMember.get();
            boolean isActive = existingMember.getIsActive();
            // 계정이 이미 비활성화 된 경우
            if (!isActive) {
                return ResponseEntity.ok().body("이미 비활성화 된 계정입니다!");
            }
            // 계정이 비활성화 된 경우가 아닐때
            existingMember.deActivateMember();
            return ResponseEntity.ok().body("계정이 비활성화 되었습니다. ");
        }
        // 엑세스 토큰으로부터 가져온 memberId 에 해당하는 회원이 없을 때
        return ResponseEntity.badRequest().body("존재하지 않는 Id 에 대한 비활성화 요청입니다. ");
    }

    /**
     * 회원 재활성화
     */
    @PostMapping("/reactivate/{memberId}")
    public ResponseEntity<?> reActivateMember(@PathVariable Long memberId, HttpServletRequest request) {
        // 회원 비활성화 조건 : 접속 유저 id == request.user.getId()
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        // memberId 로 회원 조회
        Optional<Member> optionalMember = memberRepositoryV1.findById(memberId);
        if (optionalMember.isPresent()) {
            Member existingMember = optionalMember.get();
            boolean isActive = existingMember.getIsActive();
            if (isActive) {
                return ResponseEntity.ok().body("이미 활성화된 계정입니다!");
            }
            existingMember.activateMember();
            return ResponseEntity.ok().body("계정이 활성화되었습니다. ");
        }
        return ResponseEntity.badRequest().body("존재하지 않는 Id 로의 활성화 요청입니다. ");
    }

    /**
     * 1. 로그인 유저의 id와 경로에 있는 PathVariable memberId 가 같아야, 비밀번호 재설정이 가능합니다.
     * 2. url 경로의 memberId 가 , 회원가입되어 있는 유저인지 확인합니다.
     * 3. 입력한 비밀번호가, DB의 비밀번호와 일치하는지 확인합니다.
     * 4. 새롭게 입력한 newPassword와 newPassword_confirmation 가 같으면, 유저의 비밀번호를 갱신합니다.
     */
    @PostMapping("/repassword/{memberId}")
    public ResponseEntity<?> rePasswordMember(@PathVariable Long memberId, HttpServletRequest request, @RequestBody ResetPasswordRequest passwordRequest) {
        // 회원 비활성화 조건 : 접속 유저 id == request.user.getId()
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        // memberId 로 회원 조회
        Member existingMember = findExistingMember(memberId);
        if(existingMember == null){
            return responseStatusAndMessage(HttpStatus.BAD_REQUEST, "회원가입 되지 않은 유저입니다. 해당 url의 memberId는 서버에 존재하지 않습니다.");
        }

        if (!isPasswordCorrect(passwordRequest.getPassword(), existingMember)) {
            return responseStatusAndMessage(HttpStatus.BAD_REQUEST, "입력하신 비밀번호가 잘못되었습니다. 다른 비밀번호입니다.");
        }

        resetMemberPassword(passwordRequest, existingMember);
        return responseStatusAndMessage(HttpStatus.OK, "계정의 비밀번호가 재설정되었습니다.");

    }

    private ResponseEntity<?> responseStatusAndMessage(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }

    private Member findExistingMember(Long memberId) {
        Optional<Member> optionalMember = memberRepositoryV1.findById(memberId);
        return optionalMember.orElse(null);
    }

    private boolean isPasswordCorrect(String password, Member existingMember) {
        return existingMember.getPassword().equals(password);
    }

    private void resetMemberPassword(ResetPasswordRequest passwordRequest, Member existingMember) {
        if (isNewPasswordsMatch(passwordRequest)) {
            existingMember.reSetPassword(passwordRequest.getNew_password());
            memberRepositoryV1.save(existingMember);
        }
    }

    private boolean isNewPasswordsMatch(ResetPasswordRequest passwordRequest) {
        return passwordRequest.getNew_password().equals(passwordRequest.getNew_password_confirmation());
    }

    // 예외 처리를 위한 메소드
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        // 회원을 찾지 못한 경우 404 Not Found 응답과 함께 예외 메시지를 반환합니다.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @Data
    private static class ResetPasswordRequest {
        private String password;
        private String new_password;
        private String new_password_confirmation;
    }

}
