package PU.pushop.profile.entity;

import PU.pushop.address.Addresses;
import PU.pushop.members.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String profileImgName;
    private String profileImgPath;

    @Lob
    private String introduction;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    private List<Addresses> addresses = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Profiles(Member member, String introduction, String profileImgName, String profileImgPath, List<Addresses> addresses) {
        this.member = member;
        this.introduction = introduction;
        this.profileImgName = profileImgName;
        this.profileImgPath = profileImgPath;
        this.addresses = addresses;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 최초 회원가입 시, 기본적으로 만들어주는 프로필.
    public static Profiles createMemberProfile(Member member) {
        return new Profiles(member, "자기 소개를 수정해주세요. ", null, null, List.of());
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

}
