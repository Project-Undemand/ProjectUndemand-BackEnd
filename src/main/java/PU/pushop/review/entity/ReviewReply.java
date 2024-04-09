package PU.pushop.review.entity;

import PU.pushop.members.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "review_reply")
public class ReviewReply {
    @Id
    @SequenceGenerator(
            name = "review_reply_sequence",
            sequenceName = "review_reply_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "review_reply_sequence"
    )
    @Column(name = "reply_id")
    private Long reviewReplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_by", nullable = false)
    private Member replyBy;

    @Column(name = "reply_content", nullable = false)
    private String replyContent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate createdAt;

    public ReviewReply() {
        this.createdAt = LocalDate.now();
    }

}
