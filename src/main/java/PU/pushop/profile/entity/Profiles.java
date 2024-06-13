package PU.pushop.profile.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.profile.entity.enums.MemberAges;
import PU.pushop.profile.entity.enums.MemberGender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_profile")
public class Profiles {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    private String profileImgName;
    @Setter
    private String profileImgPath;

    @Lob
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_ages")
    private MemberAges memberAges;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_gender")
    private MemberGender memberGender;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Profiles(Member member, String introduction, String profileImgName, String profileImgPath) {
        this.member = member;
        this.introduction = introduction;
        this.profileImgName = profileImgName;
        this.profileImgPath = profileImgPath;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 최초 회원가입 시, 기본적으로 만들어주는 프로필.
    public static Profiles createMemberProfile(Member member) {
        return new Profiles(member, "자기 소개를 수정해주세요. ", null, null);
    }

    public void updateDateTime(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateProfileImgPath(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }

    public void updateProfileImgName(String profileImgName) {
        this.profileImgName = profileImgName;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setProfileImage(byte[] imageBytes) {
        setProfileImagePathAndImageName(new String(imageBytes));
    }

    private void setProfileImagePathAndImageName(String imagePath) {
        this.profileImgPath = imagePath;
        this.profileImgName = this.profileImgPath.substring(this.profileImgPath.lastIndexOf("/") + 1);
    }

    public void updateMemberAge(MemberAges newAge) {
        this.memberAges = newAge;
    }

    public void updateMemberGender(MemberGender newGender) {
        this.memberGender = newGender;
    }
}
