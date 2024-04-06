package PU.pushop.product.entity;

import PU.pushop.members.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wish_list")
public class WishList {
    @Id
    @SequenceGenerator(
            name = "wishlist_sequence",
            sequenceName = "wishlist_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wishlist_sequence"
    )
    @Column(name = "wishlist_id")
    private Long wishListId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
