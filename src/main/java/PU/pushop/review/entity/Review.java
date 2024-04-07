package PU.pushop.review.entity;

import PU.pushop.payment.entity.PaymentHistory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "review")
public class Review {
    @Id
    @SequenceGenerator(
            name = "review_sequence",
            sequenceName = "review_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "review_sequence"
    )
    @Column(name = "review_id")
    private Long reviewId;

//    @PrimaryKeyJoinColumn(name = "payment")
    @OneToOne
    @JoinColumn(name = "payment_history_id")
    private PaymentHistory paymentHistory;

    @Column(name = "title", nullable = false)
    private String reviewTitle;

    @Column(name = "content", nullable = false)
    private String reviewContent;

    @Column(name = "rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private int rating;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate createdAt;

    public Review() {
        this.createdAt = LocalDate.now();
    }


}
