package PU.pushop.profile;


import lombok.Data;



@Data
public class MemberProfileDto {

    private Long profileId;
    private MemberDTO member;
    private String profileImgName;
    private String profileImgPath;
    private String introduction;
    private String addresses;
    private String createdAt;
    private String updatedAt;
}
