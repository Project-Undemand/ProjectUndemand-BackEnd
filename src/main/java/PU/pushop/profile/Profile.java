package PU.pushop.profile;

import PU.pushop.address.Address;
import PU.pushop.members.entity.Member;
import PU.pushop.review.entity.Review;
import PU.pushop.wishList.entity.WishList;
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
@Table(name = "profile")
public class Profile {

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id")
    private List<WishList> wishLists = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Profile(Member member, String introduction, String profileImgName, String profileImgPath, List<WishList> wishLists, List<Review> reviews, List<Address> addresses) {
        this.member = member;
        this.introduction = introduction;
        this.profileImgName = profileImgName;
        this.profileImgPath = profileImgPath;
        this.wishLists = wishLists;
        this.reviews = reviews;
        this.addresses = addresses;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 최초 회원가입 시, 기본적으로 만들어주는 프로필.
    public static Profile createMemberProfile(Member member) {
        return new Profile(member, "자기 소개를 수정해주세요. ", null, null, List.of(), List.of(), List.of());
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

//    public static Profile CreateProfile(String introduction) {
//        return new Profile(introduction, )
//    }
}
