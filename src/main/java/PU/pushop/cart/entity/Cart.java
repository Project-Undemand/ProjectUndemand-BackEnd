package PU.pushop.cart.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.productManagement.entity.ProductManagement;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Entity
@Getter
@Table(name = "cart")
public class Cart {

    /*@Id
    @SequenceGenerator(
            name = "cart_sequence",
            sequenceName = "cart_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_sequence"
    )*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "Product_Mgt_id", nullable = false)
    private ProductManagement productManagement;

    @Column(name = "quantity", nullable = false)
    @Min(value = 0L)
    private Long quantity;

    @Column(name = "price", nullable = false)
    private Long price;

    public Cart(Member member, ProductManagement productManagement, Long quantity, Long price) {
        this.member = member;
        this.productManagement = productManagement;
        this.quantity = quantity;
        this.price = price;
    }

    public Cart() {

    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

}
