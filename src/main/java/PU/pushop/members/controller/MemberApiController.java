package PU.pushop.members.controller;

import PU.pushop.members.entity.Member;
import PU.pushop.members.model.LoginRequest;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberRepositoryV1 memberRepositoryV1;
    private final JoinService joinService;

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

        Member member = joinService.memberLogin(loginRequest);
        if(member == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("email 또는 비밀번호가 일치하지 않습니다!");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("로그인 성공했습니다");
    }

    // 예외 처리를 위한 메소드
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
        // 회원을 찾지 못한 경우 404 Not Found 응답과 함께 예외 메시지를 반환합니다.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
