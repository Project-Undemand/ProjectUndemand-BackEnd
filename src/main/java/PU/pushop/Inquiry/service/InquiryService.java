package PU.pushop.Inquiry.service;


import PU.pushop.Inquiry.model.InquiryCreateDto;
import PU.pushop.Inquiry.model.InquiryUpdateDto;
import PU.pushop.global.authentication.jwts.login.CustomUserDetails;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.product.entity.Product;
import PU.pushop.Inquiry.repository.InquiryRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import PU.pushop.Inquiry.model.InquiryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class InquiryService {
    public final InquiryRepository inquiryRepository;
    public final MemberRepositoryV1 memberRepository;
    public final ProductRepositoryV1 productRepository;
    private final JWTUtil jwtUtil;
    public final ModelMapper modelMapper;

    /**
     * 문의 등록
     * @return
     */

    @Transactional
    public Long createInquiry(InquiryCreateDto requestDto, Long productId, HttpServletRequest request) {

        Inquiry inquiry = modelMapper.map(requestDto, Inquiry.class);

        // 멤버 저장
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long memberId = userDetails.getMemberId();
            System.out.println(userDetails.getMemberId());

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다. Id : " + memberId));
            inquiry.setMember(member);
            inquiry.setName(member.getUsername());
            inquiry.setEmail(member.getEmail());
        } else {
            inquiry.setMember(null);
        }

        // 상품 저장
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("해당 상품을 찾을 수 없습니다. Id : " + productId));
        inquiry.setProduct(product);

        // BD에 저장
        inquiryRepository.save(inquiry);
        return inquiry.getInquiryId();
    }

    /**
     * 전체 문의 리스트
     * @return
     */
    public List<InquiryDto> allInquiryList() {
        List<InquiryDto> inquiryDtoList = new ArrayList<>();
        List<Inquiry> inquiryList = inquiryRepository.findAll();

        for (Inquiry inquiry : inquiryList) {
            inquiryDtoList.add(InquiryDto.mapInquiryToDto(inquiry, false));
        }

        return inquiryDtoList;
    }

    /**
     * 특정 상품의 문의글 리스트
     * @param productId
     * @return
     */
    public List<InquiryDto> inquiryListByProductId(Long productId) {
        return inquiryRepository.findByProduct_ProductId(productId).stream()
                .map(inquiry -> InquiryDto.mapInquiryToDto(inquiry, false))
                .collect(Collectors.toList());
    }

    /**
     * 문의글 상세보기
     * @param inquiryId
     * @return
     */
    public InquiryDto inquiryDetail(Long inquiryId) {
        Inquiry inquiryDetail = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NoSuchElementException("글을 찾을 수 없습니다."));


        return InquiryDto.mapInquiryToDto(inquiryDetail, true);
    }

    /**
     * 문의글 수정
     * @param inquiryId
     * @param requestDto
     * @param password
     * @return
     */
    public Inquiry updateInquiry(Long inquiryId, InquiryUpdateDto requestDto, String password) {

        Inquiry existingInquiry = validatePasswordAndGetInquiry(inquiryId, password);

        Inquiry newInquiry = modelMapper.map(requestDto, Inquiry.class);

        existingInquiry.setInquiryType(newInquiry.getInquiryType());
        existingInquiry.setInquiryContent(newInquiry.getInquiryContent());

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
                .orElseThrow(() -> new NoSuchElementException("글을 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!existingInquiry.getPassword().equals(password)) {
            throw new IllegalArgumentException("올바른 비밀번호가 아닙니다.");
        }

        return existingInquiry;
    }

}
