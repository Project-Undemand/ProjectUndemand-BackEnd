package PU.pushop.profile.controller;

import PU.pushop.global.authorization.MemberAuthorizationUtil;
import PU.pushop.members.service.MemberService;
import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.entity.enums.MemberAges;
import PU.pushop.profile.entity.enums.MemberGender;
import PU.pushop.profile.model.MemberDTO;
import PU.pushop.profile.model.MemberProfileDto;
import PU.pushop.profile.repository.ProfileRepository;
import PU.pushop.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileControllerV1 {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberProfileDto> getProfile(@PathVariable Long memberId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        Optional<Profiles> memberProfile = getMemberProfileByMemberId(memberId);

        if (memberProfile.isPresent()) {
            Profiles profiles = memberProfile.get();
            MemberDTO memberDTO = MemberDTO.createMemberDto(profiles.getMember());
            MemberProfileDto memberProfileDto = MemberProfileDto.createMemberProfileDto(profiles, memberDTO);

            return ResponseEntity.ok(memberProfileDto);
        }
        return ResponseEntity.notFound().build();
    }


    public Optional<Profiles> getMemberProfileByMemberId(Long memberId) {
        Optional<Profiles> memberProfileOpt = profileRepository.findByMemberId(memberId);
        return memberProfileOpt;
    }

    @GetMapping("/image/{memberId}")
    public ResponseEntity<?> getProfileImage(@PathVariable Long memberId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
        return ResponseEntity.ok().body(profileService.getProfileImage(memberId));
    }

    @PostMapping("/image/{memberId}")
    public ResponseEntity<String> postProfileImage(@PathVariable Long memberId, @RequestParam("imageFile") MultipartFile imageFile) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        return profileService.uploadProfileImageV3(memberId, imageFile);
    }

    @DeleteMapping("/image/{memberId}")
    public ResponseEntity<String> deleteProfileImage(@PathVariable Long memberId) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        try {
            profileService.deleteProfileImage(memberId);
            return ResponseEntity.ok().body("Profile image deleted successfully for member id : " + memberId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the image");
        }
    }


    /**
     * 1. request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
     * 2. 회원의 introduction 을 변경하고 저장
     * @param memberId
     * @param newIntroduction
     * @return
     */
    @PutMapping("/{memberId}/introduction")
    public ResponseEntity<String> updateMemberIntroduction(@PathVariable Long memberId, @RequestBody String newIntroduction) {
        // RequestBody 로 건너온 introduction 값을 정리
        newIntroduction = newIntroduction.replace("\"", "");

        // request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);

        // memberId 를 통해 Profiles 조회
        boolean updateSuccess = profileService.updateIntroduction(memberId, newIntroduction);
        if (updateSuccess) {
            return ResponseEntity.ok("Member Introduction updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * 1. request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
     * 2. 회원의 username 을 변경하고 저장
     * @param memberId
     * @param newUsername
     * @return
     */
    @PutMapping("/{memberId}/username")
    public ResponseEntity<String> updateMemberUsername(@PathVariable Long memberId, @RequestBody String newUsername) {
        // RequestBody 로 건너온 Nickname 을 enum 타입으로 변경
        newUsername = newUsername.replace("\"", "");
        String maskedUsername = memberService.maskName(newUsername);
        // request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        // memberId 를 통해 member 조회
        boolean updateSuccess = profileService.updateUsername(memberId, maskedUsername);
        if (updateSuccess) {
            return ResponseEntity.ok("Member Username updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 1. request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
     * 2. 회원의 nickname 을 변경하고 저장
     * @param memberId
     * @param newNickname
     * @return
     */
    @PutMapping("/{memberId}/nickname")
    public ResponseEntity<String> updateMemberNickname(@PathVariable Long memberId, @RequestBody String newNickname) {
        // RequestBody 로 건너온 Nickname 을 enum 타입으로 변경
        newNickname = newNickname.replace("\"", "");
        // request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        // memberId 를 통해 member 조회
        boolean updateSuccess = profileService.updateNickname(memberId, newNickname);
        if (updateSuccess) {
            return ResponseEntity.ok("Member Nickname updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{memberId}/age")
    public ResponseEntity<String> updateMemberAge(@PathVariable Long memberId, @RequestBody String newAge) {
        // RequestBody 로 건너온 newAge 이 어떻게 넘어오는지 체크
        newAge = newAge.replace("\"", "");
        // request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        // memberId 를 통해 member 조회
        MemberAges memberAges = MemberAges.valueOf(newAge);
        boolean updateSuccess = profileService.updateAge(memberId, memberAges);
        if (updateSuccess) {
            return ResponseEntity.ok("Member Age updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{memberId}/gender")
    public ResponseEntity<String> updateMemberGender(@PathVariable Long memberId, @RequestBody String newGender) {
        // RequestBody 로 건너온 newGender 이 어떻게 넘어오는지 체크
        newGender = newGender.replace("\"", "");
        // request.user.id 가 요구하는 프로필 정보의 id 과 같은지 체크
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        // memberId 를 통해 member 조회
        MemberGender memberGender = MemberGender.valueOf(newGender);
        boolean updateSuccess = profileService.updateGender(memberId, memberGender);
        if (updateSuccess) {
            return ResponseEntity.ok("Member Gender updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
