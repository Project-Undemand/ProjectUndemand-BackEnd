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
public class InquiryCreateDto {
    private Long memberId;
    
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    private String email;
    private InquiryType inquiryType;
    @NotBlank(message = "제목은 필수입니다.")
    private String inquiryTitle;
    @NotBlank(message = "내용은 필수입니다.")
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

    public static InquiryCreateDto requestForm(InquiryCreateDto request) {
        InquiryCreateDto inquiryCreateDto = new InquiryCreateDto();

        inquiryCreateDto.setMemberId(request.getMemberId());
        inquiryCreateDto.setName(request.getName());
        inquiryCreateDto.setEmail(request.getEmail());
        inquiryCreateDto.setInquiryType(request.getInquiryType());
        inquiryCreateDto.setInquiryTitle(request.getInquiryTitle());
        inquiryCreateDto.setInquiryContent(request.getInquiryContent());
        inquiryCreateDto.setPassword(request.getPassword());
        inquiryCreateDto.setIsSecret(request.getIsSecret());

        return inquiryCreateDto;
    }

    public static Inquiry setInquiry(InquiryCreateDto inquiryCreateDto) {
        Inquiry inquiry = new Inquiry();

        inquiry.setName(inquiryCreateDto.getName());
        inquiry.setEmail(inquiryCreateDto.getEmail());
        inquiry.setInquiryType(inquiryCreateDto.getInquiryType());
        inquiry.setInquiryTitle(inquiryCreateDto.getInquiryTitle());
        inquiry.setInquiryContent(inquiryCreateDto.getInquiryContent());
        inquiry.setPassword(inquiryCreateDto.getPassword());
        inquiry.setIsSecret(inquiryCreateDto.getIsSecret());

        return inquiry;
    }

}
