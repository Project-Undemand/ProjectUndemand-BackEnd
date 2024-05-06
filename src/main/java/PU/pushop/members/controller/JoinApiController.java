package PU.pushop.members.controller;


import PU.pushop.global.mail.service.EmailMemberService;
import PU.pushop.members.entity.Member;
import PU.pushop.members.model.LoginRequest;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import PU.pushop.members.service.MemberService;
import PU.pushop.profile.MemberProfile;
import PU.pushop.profile.ProfileRepository;
import PU.pushop.profile.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialNotFoundException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JoinApiController {

    private final MemberService memberService;
    private final EmailMemberService emailMemberService;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshRepository refreshRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    /**
     * 2024.04.22 리펙토링 (김성우) - Profile 추가, return : id -> email
     * 일반 회원에 대한 회원가입 진행. (default) MemberRole = USER, SocialType = GENERAL,
     * 이메일 인증 로직 추가.
     * @param request email, password, nickname
     * @return email
     */
    @PostMapping("/join")
    @Transactional
    public ResponseEntity<?> joinMemberV1(@RequestBody @Valid JoinMemberRequest request) {
        // 표준화된 128-bit의 고유 식별자
        String token = UUID.randomUUID().toString();

        // request로부터 받은 데이터로 Member 객체 생성.
        Member member = createMemberFromRequest(request, token);

        String requestNickname = request.getNickname();
        // 가입할 유저가 입력한 nickname 이 없거나, 비어있으면 400 응답을 반환합니다. [2024.04.16 김성우 추가]
        if (requestNickname == null || requestNickname.isEmpty()) {
            return new ResponseEntity<>("Nickname cannot be empty", HttpStatus.BAD_REQUEST);
        }

        // 동일한 이메일이 존재하는지 유효성 검사. 백엔드 로그에 ERROR 전달.[2024.04.14 김성우 추가]
        try {
            validateExistedMemberByEmail(member.getEmail());
        } catch (MemberService.ExistingMemberException e) {
            // 클라이언트에게 400 Bad Request 오류를 반환.
            return ResponseEntity.badRequest().body(member.getEmail() + " : 이미 등록된 이메일입니다.");
        }
        // 회원 가입 시 , 회원 저장 및 프로필 저장을 한꺼번에 실행합니다. (같은 트렌젝션 내에서 처리)
        Member joinMember = memberService.joinMember(member);

        // 멤버 데이터로, 프로필 생성
        MemberProfile profile = MemberProfile.createMemberProfile(joinMember);
        profileRepository.save(profile);

        // 회원가입 완료 후 이메일 인증 메일 전송
        sendVerificationEmail(joinMember.getEmail(), token);

        JoinMemberResponse response = new JoinMemberResponse(joinMember.getId(), member.getEmail());
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
            log.error("이미 등록된 이메일입니다.");
            throw new MemberService.ExistingMemberException();
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


    @PostMapping("/login")
    @Transactional
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
    @Transactional
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request,
                                    HttpServletResponse response) {
        System.out.println(refreshToken);
        if (refreshToken != null) {
            log.info(refreshToken + "is not null");

            Optional<Member> optionalMember = memberRepositoryV1.findByToken(refreshToken);
            if (optionalMember.isPresent()) {
                log.info("optionalMember " + "is Present! 존재하는 유저에 대한 로그아웃을 실행합니다. ");
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


    /**
     * 2024.04.22 리펙토링 (김성우) - Profile 추가, return : id -> email
     * 어드민 회원에 대한 회원가입 진행. (default) MemberRole = ADMIN, SocialType = GENERAL,
     * 이메일 인증 로직 제외.
     * @param request email, password, nickname
     * @return email
     */
    @PostMapping("/admin/join")
    @Transactional
    public ResponseEntity<?> joinAdmin(@RequestBody @Valid JoinMemberRequest request) {
//        validatePasswordMatch(request.getPassword(), request.getPassword_certify());
        // 표준화된 128-bit의 고유 식별자
        String token = UUID.randomUUID().toString();

        // ADMIN 을 따로 생성하는 페이지를 따로 구성해서, 진행시킬 예정.
        Member member = createAdminFromRequest(request, token);
        member.verifyAdminUser();

        String requestNickname = request.getNickname();
        // 가입할 유저가 입력한 nickname 이 없거나, 비어있으면 400 응답을 반환합니다.
        if (requestNickname == null || requestNickname.isEmpty()) {
            return new ResponseEntity<>("Nickname cannot be empty", HttpStatus.BAD_REQUEST);
        }
        // 동일한 이메일이 존재하는지 유효성 검사.
        try {
            validateExistedMemberByEmail(member.getEmail());
        } catch (MemberService.ExistingMemberException e) {
            // 클라이언트에게 400 Bad Request 오류를 반환.
            return ResponseEntity.badRequest().build();
        }
        // 멤버 객체를 가지고 회원가입 Join 서비스 진행.
        Member newAdminMember = memberRepositoryV1.save(member);

        // 멤버 데이터로, 마이 프로필 생성
        MemberProfile profile = MemberProfile.createMemberProfile(newAdminMember);
        profileRepository.save(profile);

        JoinMemberResponse response = new JoinMemberResponse(newAdminMember.getId(), member.getEmail());
        // 회원가입 진행한 멤버의 id만 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Member createMemberFromRequest(JoinMemberRequest request, String token) {
        return Member.createGeneralMember(
                request.email,
                request.nickname,
                passwordEncoder.encode(request.password),
                token
        );
    }

    private Member createAdminFromRequest(JoinMemberRequest request, String token) {
        return Member.createAdminMember(
                request.email,
                request.nickname,
                passwordEncoder.encode(request.password),
                token
        );
    }

    @Data
    private static class JoinMemberRequest {
        private String email;
        private String nickname;
        private String password;
    }

    @Data
    private static class JoinMemberResponse {
        private Long memberId;
        private String email;

        public JoinMemberResponse(Long memberId, String email) {
            this.memberId = memberId;
            this.email = email;
        }
    }

}
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public class PasswordMismatchException extends IllegalArgumentException {
//        public PasswordMismatchException() {
//            super("Password and password confirmation do not match");
//        }
//    }


