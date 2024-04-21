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
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

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

    public Profile(Member member, String introduction, List<WishList> wishLists, List<Review> reviews, List<Address> addresses, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.member = member;
        this.introduction = introduction;
        this.wishLists = wishLists;
        this.reviews = reviews;
        this.addresses = addresses;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDateTime(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

//    public static Profile CreateProfile(String introduction) {
//        return new Profile(introduction, )
//    }
}
