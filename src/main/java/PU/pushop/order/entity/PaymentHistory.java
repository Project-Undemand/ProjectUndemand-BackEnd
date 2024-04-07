package PU.pushop.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payment")
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

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "postcode")
    private String postCode;

    @Column(name = "tel")
    private String phone;

    @Column(name = "merchant_uid")
    private String merchantUid;

    @Column(name = "product_names")
    private String productName;

    @Column
    private Long price;

    @Column(name = "paid_at")
    private LocalDate paidAt;

    @Column(name = "pay_method")
    private String payMethod;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_uid")
    private String paymentUid; // 결제 고유 번호



}
