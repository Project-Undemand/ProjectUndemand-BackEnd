package PU.pushop.order.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.order.entity.enums.PayMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "order")
public class Order {
    @Id
    @SequenceGenerator(
            name = "order_sequence",
            sequenceName = "order_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_sequence"
    )
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Enumerated(EnumType.STRING)
    PayMethod payMethod;

    @Column(nullable = false, length = 100)
    private String impUid; //포트원 거래고유번호

    @Column(nullable = false, length = 100)
    private String merchantUid;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "post_code", nullable = false, length = 100)
    private String postCode;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate createdAt;

    public Order() {
        this.createdAt = LocalDate.now();
    }
}
