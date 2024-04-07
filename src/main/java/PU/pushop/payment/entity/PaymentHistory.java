package PU.pushop.payment.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.order.entity.Orders;
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
    @JoinColumn(name = "member")
    private Member member;

    @OneToOne
    @JoinColumn(name = "orders")
    private Orders orders;

    @Column
    private Long price;

    @Column(name = "paid_at")
    private LocalDate paidAt;

    @Column(name = "status")
    private Boolean status = true;


    public PaymentHistory() {
        this.paidAt =  LocalDate.now();
    }


}
