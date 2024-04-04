package PU.pushop.product.service;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.product.entity.Inquiry;
import PU.pushop.product.entity.Product;
import PU.pushop.product.repository.InquiryRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import PU.pushop.product.model.InquiryDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {
    public final InquiryRepository inquiryRepository;
    public final MemberRepositoryV1 memberRepository;
    public final ProductRepositoryV1 productRepository;

    /**
     * 문의 등록
     * @param inquiry
     * @return inquiryId
     */
    @Transactional
    public Long createInquiry(Inquiry inquiry, Long productId) {
//        Optional<Member> member = memberRepository.findById(memberId);
        Optional<Product> product = productRepository.findByProductId(productId);
//        inquiry.setMember(member.orElse(null));
        inquiry.setProduct(product.orElse(null));
        inquiryRepository.save(inquiry);
        return inquiry.getInquiryId();
    }

    /**
     * 전체 문의 리스트
     * @return
     */
    public List<InquiryDto> allInquiryList() {
        return inquiryRepository.findAll().stream()
                .map(this::mapInquiryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품의 문의글 리스트
     * @param productId
     * @return
     */
    public List<InquiryDto> inquiryListByProductId(Long productId) {
        return inquiryRepository.findByProduct_ProductId(productId).stream()
                .map(this::mapInquiryToDto)
                .collect(Collectors.toList());
    }

    private InquiryDto mapInquiryToDto(Inquiry inquiry) {
        InquiryDto inquiryDto = new InquiryDto();
        inquiryDto.setInquiryId(inquiry.getInquiryId());
        inquiryDto.setMemberId(inquiry.getMember() != null ? inquiry.getMember().getId() : null);
        inquiryDto.setProductId(inquiry.getProduct().getProductId());
        inquiryDto.setName(inquiry.getName());
        inquiryDto.setEmail(inquiry.getEmail());
        inquiryDto.setInquiryType(inquiry.getInquiryType());
        inquiryDto.setInquiryTitle(inquiry.getInquiryTitle());
        inquiryDto.setCreatedAt(inquiry.getCreatedAt());
        inquiryDto.setIsSecret(inquiry.getIsSecret());
        inquiryDto.setIsAnswered(inquiry.getIsAnswered());
        return inquiryDto;
    }

    /**
     * 문의글 상세보기
     * @param inquiryId
     * @return
     */
    public InquiryDto inquiryDetail(Long inquiryId) {
        Inquiry inquiryDetail = inquiryRepository.findById(inquiryId).orElse(null);

        if (inquiryDetail == null) {
            // 처리할 로직 추가
            return null;
        }

        InquiryDto inquiryDto = new InquiryDto();
        inquiryDto.setInquiryId(inquiryDetail.getInquiryId());
        inquiryDto.setMemberId(inquiryDetail.getMember() != null ? inquiryDetail.getMember().getId() : null);
        inquiryDto.setProductId(inquiryDetail.getProduct().getProductId());
        inquiryDto.setName(inquiryDetail.getName());
        inquiryDto.setEmail(inquiryDetail.getEmail());
        inquiryDto.setInquiryType(inquiryDetail.getInquiryType());
        inquiryDto.setInquiryTitle(inquiryDetail.getInquiryTitle());
        inquiryDto.setInquiryContent(inquiryDetail.getInquiryContent());
        inquiryDto.setPassword(inquiryDetail.getPassword());
        inquiryDto.setCreatedAt(inquiryDetail.getCreatedAt());
        inquiryDto.setIsSecret(inquiryDetail.getIsSecret());
        inquiryDto.setIsAnswered(inquiryDetail.getIsAnswered());
        return inquiryDto;
    }

    /**
     * 문의글 수정
     * @param inquiryId
     * @param updatedInquiry
     * @return
     */
    public Inquiry updateInquiry(Long inquiryId, Inquiry updatedInquiry, String password) {
//        Inquiry existingInquiry = inquiryRepository.findById(inquiryId)
//                .orElseThrow(() -> new RuntimeException("글을 찾을 수 없습니다."));

        Inquiry existingInquiry = validatePasswordAndGetInquiry(inquiryId, password);

        existingInquiry.setInquiryType(updatedInquiry.getInquiryType());
        existingInquiry.setInquiryTitle(updatedInquiry.getInquiryTitle());
        existingInquiry.setInquiryContent(updatedInquiry.getInquiryContent());
        existingInquiry.setIsSecret(updatedInquiry.getIsSecret());

        return inquiryRepository.save(existingInquiry);
    }

    /**
     * 문의글 삭제
     * @param inquiryId
     */
    public void deleteInquiry(Long inquiryId, String password) {
//        Inquiry existingInquiry = inquiryRepository.findById(inquiryId)
//                .orElseThrow(() -> new RuntimeException("글을 찾을 수 없습니다."));

        Inquiry existingInquiry = validatePasswordAndGetInquiry(inquiryId, password);

        inquiryRepository.delete(existingInquiry);
    }

    /**
     * 수정/삭제시 비밀번호를 검증하고 기존 문의글 반환하는 함수
     * @param inquiryId
     * @param password
     * @return
     */
    private Inquiry validatePasswordAndGetInquiry(Long inquiryId, String password) {
        Inquiry existingInquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("글을 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!existingInquiry.getPassword().equals(password)) {
            throw new RuntimeException("올바른 비밀번호가 아닙니다.");
        }

        return existingInquiry;
    }

}
