package PU.pushop.cart.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.order.entity.Orders;
import PU.pushop.product.entity.Product;
import PU.pushop.productManagement.entity.ProductManagement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cart")
public class Cart {

    @Id
    @SequenceGenerator(
            name = "cart_sequence",
            sequenceName = "cart_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cart_sequence"
    )
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "Product_Mgt_id")
    private ProductManagement productManagement;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "price")
    private Long price;
//
//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Orders order;

}
