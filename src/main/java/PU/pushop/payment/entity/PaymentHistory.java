package PU.pushop.payment.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.order.entity.Orders;
import PU.pushop.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_history")
public class PaymentHistory {

    /*@Id
    @SequenceGenerator(
            name = "pay_sequence",
            sequenceName = "pay_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pay_sequence"
    )*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "orders", nullable = false)
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @Column(name = "impUid")
    private String impUid;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_option")
    private String productOption;

    @Column(name = "product_price", nullable = false)
    private Integer price;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "paid_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paidAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status statusType;

    @Column(name = "review")
    private Boolean review = false;


    public PaymentHistory() {
        this.paidAt =  LocalDateTime.now();
    }

    public PaymentHistory(String impUid, Member member, Orders orders, Product product, String productName, String productOption, Integer price, Long totalPrice, Status statusType) {
        this.impUid = impUid;
        this.member = member;
        this.orders = orders;
        this.product = product;
        this.productName = productName;
        this.productOption = productOption;
        this.price = price;
        this.totalPrice = totalPrice;
        this.paidAt =  LocalDateTime.now();
        this.statusType = statusType;
    }


    public void setReview(Boolean review) {
        this.review = review;
    }
}
