package PU.pushop.profile.service;


import PU.pushop.global.authorization.MemberAuthorizationUtil;
import PU.pushop.global.image.ImageUtil;
import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "profileImages", key = "#memberId")
    public String getProfileImage(Long memberId) throws Exception {
        Optional<Profiles> memberProfileOpt = profileRepository.findByMemberId(memberId);
        if (memberProfileOpt.isPresent()) {
            Profiles memberProfile = memberProfileOpt.get();
            return memberProfile.getProfileImgPath();
        } else {
            throw new Exception("Profile not found for member id : " + memberId);
        }
    }

    @CachePut(value = "profileImages", key = "#memberId")
    @Transactional
    public ResponseEntity<String> uploadProfileImageV3(Long memberId, MultipartFile imageFile) {
        MemberAuthorizationUtil.verifyUserIdMatch(memberId);
        String uploadsDir = "src/main/resources/static/uploads/profileimg/";

        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + imageFile.getOriginalFilename();
        String filePath = uploadsDir + fileName;
        String dbFilePath = "/uploads/profileimg/" + fileName;

        log.info("Original file size: " + imageFile.getSize() + " bytes");

        try {
            long start = System.currentTimeMillis();
            String resizedFileName = ImageUtil.resizeImageFile(imageFile, filePath, "jpeg");

            String resizedFilePath = uploadsDir + resizedFileName;
            Optional<Profiles> memberProfileOpt = profileRepository.findByMemberId(memberId);
            if (memberProfileOpt.isPresent()) {
                Profiles memberProfile = memberProfileOpt.get();
                memberProfile.setProfileImgName(resizedFileName);
                memberProfile.setProfileImgPath(dbFilePath);
                profileRepository.save(memberProfile);
                long end = System.currentTimeMillis();
                log.info("Time taken to save the image locally: " + (end - start) + " milliseconds");
                return ResponseEntity.ok().body(memberProfile.getProfileImgPath());
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
