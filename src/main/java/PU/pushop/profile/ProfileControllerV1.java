package PU.pushop.profile;


import PU.pushop.members.entity.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ProfileControllerV1 {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/profile/{memberId}")
    public ResponseEntity<MemberProfileDto> getProfile(@PathVariable Long memberId) {
        Optional<MemberProfile> memberProfile = getMemberProfileByMemberId(memberId);

        if (memberProfile.isPresent()) {
            try {
                String json = objectMapper.writeValueAsString(memberProfile.get());
                MemberProfileDto memberProfileDto = objectMapper.readValue(json, MemberProfileDto.class);

                return ResponseEntity.ok(memberProfileDto);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }

        return ResponseEntity.notFound().build();
    }


    public Optional<MemberProfile> getMemberProfileByMemberId(Long memberId) {
        Optional<MemberProfile> memberProfileOpt = profileRepository.findByMemberId(memberId);
        memberProfileOpt.ifPresent(memberProfile -> {
            Member member = memberProfile.getMember();
            String nickname = member.getNickname();//트랜잭션이 끝나기전에 Member 를 로드하기 위해 member.getUsername(); 사용
            System.out.println("nickname = " + nickname);
        });
        return memberProfileOpt;
    }
}
