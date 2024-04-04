package PU.pushop.Inquiry.model;

import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.entity.InquiryReply;
import PU.pushop.product.entity.enums.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDto {
    private Long inquiryId;
    private Long memberId;
    private Long productId;
    private String name;
    private String email;
    InquiryType inquiryType;
    private String inquiryTitle;
    private String inquiryContent;
    private String password;
    private LocalDate createdAt;
    private Boolean isSecret;
    private Boolean isResponse;
    private List<InquiryReplyDto> replies;



    public InquiryDto(Inquiry inquiry) {
        this(
                inquiry.getInquiryId(),
                inquiry.getMember().getId(),
                inquiry.getProduct().getProductId(),
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getInquiryType(),
                inquiry.getInquiryTitle(),
                inquiry.getInquiryContent(),
                inquiry.getPassword(),
                inquiry.getCreatedAt(),
                inquiry.getIsSecret(),
                inquiry.getIsResponse(),
                inquiry.getReplies().stream().map(InquiryReplyDto::new)
                        .collect(Collectors.toList())

        );

    }

}
