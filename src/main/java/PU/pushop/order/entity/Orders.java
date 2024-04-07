package PU.pushop.order.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.order.entity.enums.PayMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Orders {
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
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<Cart> carts = new ArrayList<>();

    @Column(name = "order_name")
    private String ordererName;

    @Column(name = "product_names")
    private String productName;

    @Enumerated(EnumType.STRING)
    PayMethod payMethod;

    @Column(length = 100, name = "merchant_uid")
    private String merchantUid;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "address")
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "post_code", length = 100)
    private String postCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "order_day", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate orderDay;

    @Column(name = "payment_status")
    private Boolean paymentStatus = false;

    public Orders() {
        this.orderDay = LocalDate.now();
    }
}
