package PU.pushop.product.model;

import PU.pushop.product.entity.Inquiry;
import PU.pushop.product.entity.enums.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDto {
    private Long inquiryId;
    private Long memberId;
    private Long productId;
    InquiryType inquiryType;
    private String inquiryTitle;
    private String inquiryContent;
    private String password;
    private LocalDate createdAt;
    private Boolean isSecret;
    private Boolean isAnswered;


    public InquiryDto(Inquiry inquiry) {
        this(
                inquiry.getInquiryId(),
                inquiry.getMember().getId(),
                inquiry.getProduct().getProductId(),
                inquiry.getInquiryType(),
                inquiry.getInquiryTitle(),
                inquiry.getInquiryContent(),
                inquiry.getPassword(),
                inquiry.getCreatedAt(),
                inquiry.getIsSecret(),
                inquiry.getIsAnswered()

        );

    }

}
