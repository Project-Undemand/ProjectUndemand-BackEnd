package PU.pushop.profile.service;


import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    // create, update, getMyProfile

    @Transactional
    public void saveProfileBySignup(Profiles memberProfile) {
        profileRepository.save(memberProfile);
    }

    public ResponseEntity<String> uploadProfileImage(Long memberId, MultipartFile imageFile) {

        try {
            Optional<Profiles> memberProfileOpt = profileRepository.findByMemberId(memberId);
            if (memberProfileOpt.isPresent()) {
                Profiles memberProfile = memberProfileOpt.get();
                byte[] imageBytes = imageFile.getBytes();
                memberProfile.setProfileImage(imageBytes);
                profileRepository.save(memberProfile);
                return ResponseEntity.ok().body("Profile image updated successfully for member id : " + memberId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile image not found for member id : " + memberId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the image");
        }
    }
}
