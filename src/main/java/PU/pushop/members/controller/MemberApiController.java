package PU.pushop.members.controller;

import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final JWTUtil jwtUtil;

    @GetMapping("/api/v1/members")
    public ResponseEntity<?> getMemberList(HttpServletRequest request) {
        // Request Header 에 담아준 Authorization 을 가져와서
        String authorization = request.getHeader("Authorization");
        // accessToken 을 꺼내주는 방식
        String accessToken = authorization.substring(7);
        // accessToken 에 있는 유저 권한을 파싱해서 가져옴
        MemberRole userRole = jwtUtil.getRole(accessToken);

        if (userRole == MemberRole.ADMIN) {
            // memberId에 해당하는 Member 정보를 조회합니다.
            List<Member> allMembers = memberRepositoryV1.findAll();

            // 회원을 찾은 경우 200 OK 응답과 함께 Member 정보를 반환합니다.
            return ResponseEntity.ok(allMembers);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자페이지에서, 관리자 권한으로만 회원 전체에 대한 조회가 가능합니다.!");
        }
    }


    @GetMapping("/api/v1/members/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable Long memberId) {
        // memberId에 해당하는 Member 정보를 조회합니다.
        Member member = memberRepositoryV1.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("id에 맞는 해당 회원이 존재하지 않습니다."));

        // 회원을 찾은 경우 200 OK 응답과 함께 Member 정보를 반환합니다.
        return ResponseEntity.ok(member);
    }


    // 예외 처리를 위한 메소드
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        // 회원을 찾지 못한 경우 404 Not Found 응답과 함께 예외 메시지를 반환합니다.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
