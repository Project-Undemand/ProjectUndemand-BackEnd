package PU.pushop.profile.controller;


import PU.pushop.members.entity.Member;
import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.model.MemberProfileDto;
import PU.pushop.profile.repository.ProfileRepository;
import PU.pushop.profile.service.ProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
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
        Optional<Profiles> memberProfile = getMemberProfileByMemberId(memberId);

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


    public Optional<Profiles> getMemberProfileByMemberId(Long memberId) {
        Optional<Profiles> memberProfileOpt = profileRepository.findByMemberId(memberId);
        memberProfileOpt.ifPresent(memberProfile -> {
            Member member = memberProfile.getMember();
            String nickname = member.getNickname(); //트랜잭션이 끝나기전에 Member 를 로드하기 위해 member.getUsername(); 사용
            System.out.println("nickname = " + nickname);
        });
        return memberProfileOpt;
    }

    @PostMapping("/profile/image/{memberId}")
    public ResponseEntity<String> postProfileImage(@PathVariable Long memberId, @RequestParam("imageFile") MultipartFile imageFile) {
        return profileService.uploadProfileImageV2(memberId, imageFile);
    }

    @DeleteMapping("/profile/image/{memberId}")
    public ResponseEntity<String> deleteProfileImage(@PathVariable Long memberId) {
        try {
            profileService.deleteProfileImage(memberId);
            return ResponseEntity.ok().body("Profile image deleted successfully for member id : " + memberId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the image");
        }
    }
}
