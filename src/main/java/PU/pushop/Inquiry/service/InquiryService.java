package PU.pushop.Inquiry.service;

import PU.pushop.Inquiry.model.InquiryReplyDto;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.product.entity.Product;
import PU.pushop.Inquiry.repository.InquiryRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import PU.pushop.Inquiry.model.InquiryDto;

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
        Optional<Product> product = productRepository.findByProductId(productId);

        inquiry.setProduct(product.
                orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId))
        );
        inquiryRepository.save(inquiry);
        return inquiry.getInquiryId();
    }

    /**
     * 전체 문의 리스트
     * @return
     */
    public List<InquiryDto> allInquiryList() {
        return inquiryRepository.findAll().stream()
                .map(inquiry -> mapInquiryToDto(inquiry, false))
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품의 문의글 리스트
     * @param productId
     * @return
     */
    public List<InquiryDto> inquiryListByProductId(Long productId) {
        return inquiryRepository.findByProduct_ProductId(productId).stream()
                .map(inquiry -> mapInquiryToDto(inquiry, false))
                .collect(Collectors.toList());
    }

    /**
     * 문의글 상세보기
     * @param inquiryId
     * @return
     */
    public InquiryDto inquiryDetail(Long inquiryId) {
        Inquiry inquiryDetail = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));


        return mapInquiryToDto(inquiryDetail, true);
    }

    /**
     * 문의글 수정
     * @param inquiryId
     * @param updatedInquiry
     * @return
     */
    public Inquiry updateInquiry(Long inquiryId, Inquiry updatedInquiry, String password) {

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
                .orElseThrow(() -> new IllegalArgumentException("글을 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!existingInquiry.getPassword().equals(password)) {
            throw new IllegalArgumentException("올바른 비밀번호가 아닙니다.");
        }

        return existingInquiry;
    }

    /**
     * 문의글 조회 시 Dto 변환해서 가져오기
     * @param inquiry
     * @param includeContent
     * @return
     */
    private InquiryDto mapInquiryToDto(Inquiry inquiry, boolean includeContent) {
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
        inquiryDto.setIsResponse(inquiry.getIsResponse());
        inquiryDto.setReplies(inquiry.getReplies().stream().map(InquiryReplyDto::new)
                .collect(Collectors.toList()));

        if (includeContent) {

            inquiryDto.setInquiryContent(inquiry.getInquiryContent());
            inquiryDto.setPassword(inquiry.getPassword());
        }

        return inquiryDto;
    }


}
