package PU.pushop.profile.model;


import PU.pushop.profile.entity.Profiles;
import PU.pushop.profile.entity.enums.MemberAges;
import PU.pushop.profile.entity.enums.MemberGender;
import lombok.Data;


@Data
public class MemberProfileDto {

    private Long profileId;
    private MemberDTO member;
    private String profileImgName;
    private String profileImgPath;
    private String introduction;
    private MemberAges memberAges;
    private MemberGender memberGender;
    private String createdAt;
    private String updatedAt;

    public MemberProfileDto(Long profileId, MemberDTO member, String profileImgName, String profileImgPath, String introduction, MemberAges memberAges, MemberGender memberGender, String createdAt, String updatedAt) {
        this.profileId = profileId;
        this.member = member;
        this.profileImgName = profileImgName;
        this.profileImgPath = profileImgPath;
        this.introduction = introduction;
        this.memberAges = memberAges;
        this.memberGender = memberGender;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MemberProfileDto createMemberProfileDto(Profiles profiles, MemberDTO memberDto) {
        return new MemberProfileDto(profiles.getProfileId(), memberDto, profiles.getProfileImgName(), profiles.getProfileImgPath(), profiles.getIntroduction(), profiles.getMemberAges(), profiles.getMemberGender(), profiles.getCreatedAt().toString(), profiles.getUpdatedAt().toString());
    }
}
