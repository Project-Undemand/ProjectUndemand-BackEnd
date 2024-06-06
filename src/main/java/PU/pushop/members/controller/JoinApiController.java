package PU.pushop.members.controller;

import PU.pushop.global.authentication.jwts.utils.CookieUtil;
import PU.pushop.global.mail.service.EmailMemberService;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.Refresh;
import PU.pushop.members.model.LoginRequest;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.members.repository.RefreshRepository;
import PU.pushop.members.service.MemberService;
import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.repository.ProfileRepository;
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
@Transactional
public class JoinApiController {

    private final MemberService memberService;
    private final EmailMemberService emailMemberService;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshRepository refreshRepository;
    private final ProfileRepository profileRepository;

    /**
     * 2024.04.22 리펙토링 (김성우) - Profile 추가, return : id -> email
     * 일반 회원에 대한 회원가입 진행. (default) MemberRole = USER, SocialType = GENERAL,
     * 이메일 인증 로직 추가.
     * @param request email, password, nickname
     * @return email
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinMemberV1(@RequestBody @Valid JoinMemberRequest request) {
        // 표준화된 128-bit의 고유 식별자
        String token = UUID.randomUUID().toString();

        // request로부터 받은 데이터로 Member 객체 생성.
        Member member = createMemberFromRequest(request, token);

        String requestNickname = request.getNickname();
        // 가입할 유저가 입력한 nickname 이 없거나, 비어있으면 400 응답을 반환합니다. [2024.04.16 김성우 추가]
        if (requestNickname == null || requestNickname.isEmpty()) {
            return new ResponseEntity<>("닉네임을 꼭 입력해야합니다. ", HttpStatus.BAD_REQUEST);
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

        // 멤버 데이터로, 마이 프로필 생성
        Profiles profile = Profiles.createMemberProfile(joinMember);
        profileRepository.save(profile);

        // 회원가입 완료 후 이메일 인증 메일 전송
        sendVerificationEmail(joinMember.getEmail(), token);

        JoinMemberResponse response = new JoinMemberResponse(joinMember.getId(), member.getEmail());
        // memberId, email 을 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<String> verifyEmailWhenMemberJoin(@RequestParam("token") String token) {
        // queryParameter 로 전해진 token 값에 대한 유효성검사 및 인증과정 진행.
        Member member = emailMemberService.updateByVerifyToken(token);
        if (member == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유효하지 않은 토큰입니다.");
        }
        log.info(String.valueOf("isCertifyByMail ? "+ member.isCertifyByMail()));
        return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
    }

    private void validateExistedMemberByEmail(String email) {
        boolean isExistMember = memberRepositoryV1.existsByEmail(email);
        if (isExistMember) {
            log.error("이미 등록된 이메일입니다.");
            throw new MemberService.ExistingMemberException();
        }
    }

    // 회원가입 이메일 인증 메일 전송
    private void sendVerificationEmail(String email, String token) {
        try {
            Member member = Member.createEmailMember(email, token); // isCertifyByMail = false
            emailMemberService.sendEmailVerification(member);
        } catch (Exception e) {
            // 이메일 전송에 실패한 경우 처리
            e.printStackTrace();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) throws UserPrincipalNotFoundException, CredentialNotFoundException {

        Member member = memberService.memberLogin(loginRequest);
        if (member != null) {
            log.info("멤버 이메일 인증 여부 : " + member.isCertifyByMail());
        }
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("가입되지 않는 email 이거나 비밀번호가 일치하지 않습니다. ");
        }
        if (!member.isCertifyByMail()) {
            return ResponseEntity.badRequest().body("이메일 인증이 되지 않은 회원입니다.");
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("PU에 오신 것을 환영합니다. ");
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshAuthorization", required = false) String refreshAuthorization, HttpServletRequest request,
                                    HttpServletResponse response) {
        String refreshToken = refreshAuthorization.substring(7);
        log.info(refreshToken);
        if (!refreshToken.isEmpty()) {
            Optional<Refresh> optionalRefresh = refreshRepository.findByRefreshToken(refreshToken);
            if (optionalRefresh.isPresent()) {
                Refresh refreshEntity = optionalRefresh.get();
                CookieUtil.deleteCookie(response, "refreshAuthorization");

                log.info("멤버 Id : " + refreshEntity.getMember().getId() + " 님이 로그아웃 하셨습니다.");
                // 로그아웃 시 , 멤버의 이메일을 String으로 반환
                return ResponseEntity.status(HttpStatus.OK).body("멤버 Id : " + refreshEntity.getMember().getId() + " 님이 로그아웃 하셨습니다.");
            } else {
                log.info("Refresh is not Present.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh is not Present.");
            }

        } else {
            log.info("이미 로그아웃 된 유저입니다. ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 로그아웃 된 유저입니다. ");
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
    public ResponseEntity<?> joinAdmin(@RequestBody @Valid JoinMemberRequest request) {
//        validatePasswordMatch(request.getPassword(), request.getPassword_certify());
        // 표준화된 128-bit의 고유 식별자
        String token = UUID.randomUUID().toString();

        // ADMIN 을 따로 생성하는 페이지를 따로 구성해서, 진행시킬 예정.
        Member member = createAdminFromRequest(request, token);
        member.verifyAdminUser();
        member.activateMember();

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
        Profiles profile = Profiles.createMemberProfile(newAdminMember);
        profileRepository.save(profile);

        JoinMemberResponse response = new JoinMemberResponse(newAdminMember.getId(), member.getEmail());
        // memberId, email 을 return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Member createMemberFromRequest(JoinMemberRequest request, String token) {
        // Generate a UUID for socialId
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String socialId = "general-" + uuid.substring(0, 12);
        return Member.createGeneralMember(
                request.email,
                request.nickname,
                passwordEncoder.encode(request.password),
                token,
                socialId
        );
    }

    private Member createAdminFromRequest(JoinMemberRequest request, String token) {
        // Generate a UUID for socialId
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String socialId = "general-" + uuid.substring(0, 12);
        return Member.createAdminMember(
                request.email,
                request.nickname,
                passwordEncoder.encode(request.password),
                token,
                socialId
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

