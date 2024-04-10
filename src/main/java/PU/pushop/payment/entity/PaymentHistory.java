package PU.pushop.payment.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.order.entity.Orders;
import PU.pushop.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @SequenceGenerator(
            name = "pay_sequence",
            sequenceName = "pay_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pay_sequence"
    )
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

    @Column(name = "product_name")
    private String productName;

    @Column(name = "option")
    private String option;

    @Column(name = "product_price", nullable = false)
    private Integer price;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "paid_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_DATE")
    private LocalDate paidAt;

    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "review")
    private Boolean review = false;


    public PaymentHistory() {
        this.paidAt =  LocalDate.now();
    }


}
