package PU.pushop.Inquiry.model;

import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.product.entity.enums.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryCreateDto {
    private Long memberId;
    private String name;
    private String email;
    private InquiryType inquiryType;
    private String inquiryTitle;
    private String inquiryContent;
    private String password;
    private Boolean isSecret;

    public InquiryCreateDto(Inquiry inquiry) {
        this(
                inquiry.getMember().getId(),
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getInquiryType(),
                inquiry.getInquiryTitle(),
                inquiry.getInquiryContent(),
                inquiry.getPassword(),
                inquiry.getIsSecret()
        );

    }

    public static Inquiry requestForm(InquiryCreateDto request) {
        Inquiry inquiry = new Inquiry();

        inquiry.setName(request.getName());
        inquiry.setEmail(request.getEmail());
        inquiry.setInquiryType(request.getInquiryType());
        inquiry.setInquiryTitle(request.getInquiryTitle());
        inquiry.setInquiryContent(request.getInquiryContent());
        inquiry.setPassword(request.getPassword());
        inquiry.setIsSecret(request.getIsSecret());

        return inquiry;
    }

}
