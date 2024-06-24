package PU.pushop.Inquiry.model;

import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.entity.enums.InquiryType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryUpdateDto {
    private InquiryType inquiryType;
    @NotBlank(message = "내용은 필수입니다.")
    private String inquiryContent;
    private String password;

    public InquiryUpdateDto(Inquiry inquiry) {
        this(
                inquiry.getInquiryType(),
                inquiry.getInquiryContent(),
                inquiry.getPassword()
        );

    }
}
