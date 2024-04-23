package PU.pushop.Inquiry.entity;

import PU.pushop.Inquiry.validation.EnumConstraint;
import PU.pushop.members.entity.Member;
import PU.pushop.product.entity.Product;
import PU.pushop.Inquiry.entity.enums.InquiryType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "inquiry_table")
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "writer_name",nullable = false)
    private String name;

    @Column(name = "writer_email",nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type", nullable = false)
    @EnumConstraint
    private InquiryType inquiryType;

    @Column(name = "inquiry_title",nullable = false)
    private String inquiryTitle;

    @Column(name = "inquiry_content",nullable = false)
    private String inquiryContent;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "is_secret")
    private Boolean isSecret = true;

    @Column(name = "is_response")
    private Boolean isResponse = false;

    @OneToMany(mappedBy = "inquiry")
    private List<InquiryReply> replies;

    public Inquiry() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Inquiry(Member member, Product product, String name, String email, InquiryType inquiryType, String inquiryTitle, String inquiryContent, String password) {
        this.member = member;
        this.product = product;
        this.name = name;
        this.email = email;
        this.inquiryType = inquiryType;
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
        this.password = password;
        this.createdAt = LocalDateTime.now();

    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setResponse(Boolean response) {
        isResponse = response;
    }

}
