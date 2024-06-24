package PU.pushop.Inquiry.entity;

import PU.pushop.members.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "inquiry_reply")
public class InquiryReply {
    @Id
    @SequenceGenerator(
            name = "inquiry_reply_sequence",
            sequenceName = "inquiry_reply_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "inquiry_reply_sequence"
    )
    @Column(name = "reply_id")
    private Long inquiryReplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_by", nullable = false)
    private Member replyBy;

    @Column(name = "reply_title", nullable = false)
    private String replyTitle;

    @Column(name = "reply_content", nullable = false)
    private String replyContent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public InquiryReply() {
        this.createdAt = LocalDateTime.now();

    }

    public InquiryReply(Inquiry inquiry, Member replyBy, String replyTitle, String replyContent) {
        this.inquiry = inquiry;
        this.replyBy = replyBy;
        this.replyTitle = replyTitle;
        this.replyContent = replyContent;
        this.createdAt = LocalDateTime.now();

    }

}
