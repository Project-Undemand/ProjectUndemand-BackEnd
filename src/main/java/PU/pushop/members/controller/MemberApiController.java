package PU.pushop.members.controller;

import PU.pushop.members.entity.Member;
import PU.pushop.members.model.LoginRequest;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import PU.pushop.members.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final MemberService memberService;
    private final RefreshRepository refreshRepository;

    @GetMapping("/api/v1/members/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable Long memberId) {
        // memberId에 해당하는 Member 정보를 조회합니다.
        Member member = memberRepositoryV1.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("id에 맞는 해당 회원이 존재하지 않습니다."));

        // 회원을 찾은 경우 200 OK 응답과 함께 Member 정보를 반환합니다.
        return ResponseEntity.ok(member);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) throws UserPrincipalNotFoundException, CredentialNotFoundException {

        Member member = memberService.memberLogin(loginRequest);
        if(member == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("email 또는 비밀번호가 일치하지 않습니다!");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("로그인 성공했습니다");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request,
                       HttpServletResponse response) {
        System.out.println(refreshToken);
        if (refreshToken != null) {
            log.info(refreshToken + "is not null");

            Optional<Member> optionalMember = memberRepositoryV1.findByToken(refreshToken);
            if (optionalMember.isPresent()) {
                log.info("optionalMember" + "is Present");
                // refreshToken을 이용하여 DB에 있는 해당 토큰을 삭제
                refreshRepository.deleteByRefreshToken(refreshToken);

                // 로그아웃 시 , 멤버의 이메일을 String으로 반환
                Member member = optionalMember.get();
                String memberEmail = member.getEmail();
                return ResponseEntity.status(HttpStatus.OK).body(memberEmail + " 로그아웃 되었습니다");
            } else {
                log.info("optionalMember" + "is not Present");
                return ResponseEntity.status(HttpStatus.OK).body("쿠키에 저장된 리프레쉬토큰의 유저가 존재하지 않습니다.");
            }

        } else {
            log.info(refreshToken + "is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 로그아웃 된 유저입니다!");
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
//        cookie.setSecure(true); // HTTPS에서만 쿠키 전송
        return cookie;
    }

    // 예외 처리를 위한 메소드
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        // 회원을 찾지 못한 경우 404 Not Found 응답과 함께 예외 메시지를 반환합니다.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
