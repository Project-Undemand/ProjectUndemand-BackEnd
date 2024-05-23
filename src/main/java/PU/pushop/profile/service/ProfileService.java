package PU.pushop.profile.service;


import PU.pushop.global.authorization.MemberAuthorizationUtil;
import PU.pushop.global.image.ImageUtil;
import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;

    // create, update, getMyProfile
    @Transactional
    public void saveProfileBySignup(Profiles memberProfile) {
        profileRepository.save(memberProfile);
    }

    public ResponseEntity<String> uploadProfileImageV1(Long memberId, MultipartFile imageFile) {
        // request 의 member 가 서버에서 인식하는 로그인유저와 일치하는지 확인합니다.
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
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

    @Transactional
    public ResponseEntity<String> uploadProfileImageV2(Long memberId, MultipartFile imageFile) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        String uploadsDir = "src/main/resources/static/uploads/profileimg/";

        // 이미지 파일 이름 생성 및 저장
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + imageFile.getOriginalFilename();
        String filePath = uploadsDir + fileName;
        String dbFilePath = "/uploads/profileimg/" + fileName;

        log.info("Original file size: " + imageFile.getSize() + " bytes");

        // 이미지 저장 및 DB 저장
        try {
            String resizedFileName = ImageUtil.resizeImageFile(imageFile, filePath, "jpeg");
            String resizedFilePath = uploadsDir + resizedFileName;
            Optional<Profiles> memberProfileOpt = profileRepository.findByMemberId(memberId);
            if (memberProfileOpt.isPresent()) {
                Profiles memberProfile = memberProfileOpt.get();
                memberProfile.setProfileImgName(resizedFileName);
                memberProfile.setProfileImgPath(dbFilePath); // 상대 경로가 아닌 절대 경로 설정
                profileRepository.save(memberProfile);
                return ResponseEntity.ok().body("Profile image updated successfully for member id : " + memberId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found for member id : " + memberId);
            }
        } catch (IOException e) {
            log.error("Error while processing the image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the image");
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Transactional
    public void deleteProfileImage(Long memberId) {
        Optional<Profiles> memberProfileOpt = profileRepository.findByMemberId(memberId);
        if (memberProfileOpt.isPresent()) {
            Profiles memberProfile = memberProfileOpt.get();
            String imagePath = "src/main/resources/static" + memberProfile.getProfileImgName();
            memberProfile.setProfileImage(null);
            profileRepository.save(memberProfile);
            deleteImageFile(imagePath);
        } else {
            throw new NoSuchElementException("Profile not found for member id : " + memberId);
        }
    }

    private void saveImage(MultipartFile image, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
    }

    public static void deleteImageFile(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Exception handling in case of file deletion error
            e.printStackTrace();
        }
    }

}
