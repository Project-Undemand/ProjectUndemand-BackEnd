package PU.pushop.product.entity;

import PU.pushop.members.entity.Member;
import PU.pushop.product.entity.enums.InquiryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "inquiry")
public class Inquiry {
    @Id
    @SequenceGenerator(
            name = "inquiry_sequence",
            sequenceName = "inquiry_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "inquiry_sequence"
    )
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "writer_name")
    private String name;

    @Column(name = "writer_email")
    private String email;

    @Enumerated(EnumType.STRING)
    private InquiryType inquiryType;

    @Column(name = "inquiry_title")
    private String inquiryTitle;

    @Column(name = "inquiry_content")
    private String inquiryContent;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate createdAt;

    private Boolean isSecret = true;

    private Boolean isAnswered = false;


    public Inquiry() {
        this.createdAt = LocalDate.now();
    }
}
