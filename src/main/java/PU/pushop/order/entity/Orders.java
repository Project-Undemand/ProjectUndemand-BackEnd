package PU.pushop.order.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.order.entity.enums.PayMethod;
import PU.pushop.order.model.OrderDto;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.productManagement.entity.ProductManagement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToMany
    @JoinTable(
            name = "orders_product_management",
            joinColumns = @JoinColumn(name = "orders_id"),
            inverseJoinColumns = @JoinColumn(name = "product_management_id")
    )
    private List<ProductManagement> productManagements = new ArrayList<>();


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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderDay;

    @Column(name = "payment_status")
    private Boolean paymentStatus = false;

    @OneToMany(mappedBy = "orders")
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    public Orders() {
        this.orderDay = LocalDateTime.now();
    }

    public Orders(Member member, List<ProductManagement> productManagements, String ordererName, String productName, BigDecimal totalPrice, String phoneNumber) {
        this.member = member;
        this.productManagements = productManagements;
        this.ordererName = ordererName;
        this.productName = productName;
        this.totalPrice = totalPrice;
        this.phoneNumber = phoneNumber;
    }


    public void orderConfirm(String merchantUid, OrderDto orderDto) {
        this.merchantUid = merchantUid;
        this.postCode = orderDto.getPostCode();
        this.address = orderDto.getAddress();
        this.detailAddress = orderDto.getDetailAddress();
        this.ordererName = orderDto.getOrdererName();
        this.phoneNumber = orderDto.getPhoneNumber();
        this.payMethod = orderDto.getPayMethod();
        this.orderDay = LocalDateTime.now();

    }

/*    public void setMember(Member member) {
        this.member = member;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setAddress(String address) {
        this.address = address;
    }*/

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


}
