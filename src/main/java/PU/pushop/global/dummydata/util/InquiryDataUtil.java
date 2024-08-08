package PU.pushop.global.dummydata.util;


import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.entity.enums.InquiryType;
import PU.pushop.Inquiry.repository.InquiryRepository;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InquiryDataUtil {

    private final InquiryRepository inquiryRepository;
    private final MemberRepositoryV1 memberRepositoryV1;
    private final ProductRepositoryV1 productRepository;

    public void createInquiries() {
        List<String> emails = Arrays.asList("user1@pushop.com", "user2@pushop.com", "user3@pushop.com");

        for (String email : emails) {
            Member member = memberRepositoryV1.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

            List<Product> products = productRepository.findAll();

            for (Product product : products) {
                for (int i = 0; i < 2; i++) {
                    InquiryType inquiryType = InquiryType.values()[(int) (Math.random() * 5)]; // RANDOMLY SELECT INQUIRY TYPE BETWEEN 0 AND 4
                    String title = getInquiryTitle(inquiryType, product.getProductId(), i);
                    String content = getInquiryContent(inquiryType, product.getProductId(), i);

                    Inquiry inquiry = Inquiry.builder()
                            .member(member)
                            .product(product)
                            .name(member.getNickname())
                            .email(member.getEmail())
                            .inquiryType(inquiryType)
                            .inquiryTitle(title)
                            .inquiryContent(content)
                            .password("password")
                            .build();

                    inquiryRepository.save(inquiry);
                }
            }
        }
    }

    private String getInquiryTitle(InquiryType inquiryType, Long productId, int index) {
        switch (inquiryType) {
            case PRODUCT:
                return "Product 에 대한 문의 " + productId + " - " + index;
            case DELIVERY:
                return "Delivery 에 대한 문의 " + productId + " - " + index;
            case RETURN:
                return "Return 에 대한 문의 " + productId + " - " + index;
            case EXCHANGE:
                return "Exchange 에 대한 문의 " + productId + " - " + index;
            case REFUND:
                return "Refund 에 대한 문의 " + productId + " - " + index;
            default:
                return "Inquiry Title " + productId + " - " + index;
        }
    }

    private String getInquiryContent(InquiryType inquiryType, Long productId, int index) {
        switch (inquiryType) {
            case PRODUCT:
                return "Product 에 대한 문의 입니다. 어떻게 하면 될까요?? 답변 부탁드립니다. " + productId + " - " + index;
            case DELIVERY:
                return "Delivery 에 대한 문의 입니다. 어떻게 하면 될까요?? 답변 부탁드립니다. " + productId + " - " + index;
            case RETURN:
                return "Return 에 대한 문의 입니다. 어떻게 하면 될까요?? 답변 부탁드립니다. " + productId + " - " + index;
            case EXCHANGE:
                return "Exchange 에 대한 문의 입니다. 어떻게 하면 될까요?? 답변 부탁드립니다. " + productId + " - " + index;
            case REFUND:
                return "Refund 에 대한 문의 입니다. 어떻게 하면 될까요?? 답변 부탁드립니다. " + productId + " - " + index;
            default:
                return "Inquiry Content " + productId + " - " + index;
        }
    }
}
