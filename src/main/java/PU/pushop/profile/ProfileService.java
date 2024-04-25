package PU.pushop.profile;


import PU.pushop.members.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    // create, update, getMyProfile

    @Transactional
    public void saveProfileBySignup(MemberProfile memberProfile) {
        profileRepository.save(memberProfile);
    }
}
