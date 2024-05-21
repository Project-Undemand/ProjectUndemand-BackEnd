package PU.pushop.profile.model;


import PU.pushop.address.Addresses;
import PU.pushop.members.entity.Member;
import PU.pushop.profile.entity.Profiles;
import lombok.Data;

import java.util.List;


@Data
public class MemberProfileDto {

    private Long profileId;
    private MemberDTO member;
    private String profileImgName;
    private String profileImgPath;
    private String introduction;
    private List<Addresses> addresses;
    private String createdAt;
    private String updatedAt;

    public MemberProfileDto(Long profileId, MemberDTO member, String profileImgName, String profileImgPath, String introduction, List<Addresses> addresses, String createdAt, String updatedAt) {
        this.profileId = profileId;
        this.member = member;
        this.profileImgName = profileImgName;
        this.profileImgPath = profileImgPath;
        this.introduction = introduction;
        this.addresses = addresses;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MemberProfileDto createMemberProfileDto(Profiles profiles, MemberDTO memberDto) {
        return new MemberProfileDto(profiles.getProfileId(), memberDto, profiles.getProfileImgName(), profiles.getProfileImgPath(), profiles.getIntroduction(), profiles.getAddresses(), profiles.getCreatedAt().toString(), profiles.getUpdatedAt().toString());
    }
}
