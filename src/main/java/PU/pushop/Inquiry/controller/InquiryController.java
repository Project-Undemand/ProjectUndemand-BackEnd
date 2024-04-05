package PU.pushop.Inquiry.controller;

import PU.pushop.members.entity.Member;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.product.entity.enums.InquiryType;
import PU.pushop.Inquiry.model.InquiryDto;
import PU.pushop.Inquiry.service.InquiryService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final MemberRepositoryV1 memberRepository;

    // Request Data
    @Data
    static class InquiryRequest {
        private Long productId;
        private Long memberId;
        private String name;
        private String email;
        private InquiryType inquiryType;
        private String inquiryTitle;
        private String inquiryContent;
        private String password;
        private Boolean isSecret;
    }

    private Inquiry InquiryFormRequest(InquiryRequest request) {
        Inquiry inquiry = new Inquiry();
        Member member = null;
        if (request.getMemberId() != null) {
            member = memberRepository.findById(request.getMemberId()).orElse(null);
        }
        inquiry.setMember(member);
        inquiry.setName(request.getName());
        inquiry.setEmail(request.getEmail());
        inquiry.setInquiryType(request.getInquiryType());
        inquiry.setInquiryTitle(request.getInquiryTitle());
        inquiry.setInquiryContent(request.getInquiryContent());
        inquiry.setPassword(request.getPassword());
        inquiry.setIsSecret(request.getIsSecret());

        return inquiry;
    }

    /**
     * 전체 문의글 보기
     * @return
     */
    @GetMapping("")
    public List<InquiryDto> getAllInquiries() {
        return inquiryService.allInquiryList();
    }

    /**
     * 특정 상품 문의글 리스트
     * @param productId
     * @return
     */
    @GetMapping("/list/{productId}")
    public List<InquiryDto> getProductInquiries(@PathVariable Long productId) {
        return inquiryService.inquiryListByProductId(productId);
    }


    /**
     * 문의글 작성
     * @param request
     * @param productId
     * @return
     */
    @PostMapping("/new/{productId}")
    public ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryRequest request, @PathVariable Long productId) {
        Inquiry inquiry = InquiryFormRequest(request);
//        Member member = memberService.findById(request.getMemberId());
        Long createdId = inquiryService.createInquiry(inquiry,productId);
        return ResponseEntity.ok(createdId);
    }

    /**
     * 문의글 상세보기
     * @param inquiryId
     * @return
     */
    @GetMapping("/{inquiryId}")
    public ResponseEntity<?> getInquiryById(@PathVariable Long inquiryId) {
        try {
            InquiryDto inquiryDetail = inquiryService.inquiryDetail(inquiryId);
            return new ResponseEntity<>(inquiryDetail, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * 문의글 수정
     * @param inquiryId
     * @param request
     * @return
     */
    @PutMapping("/{inquiryId}")
    public ResponseEntity<?> updateInquiry(@PathVariable Long inquiryId, @Valid @RequestBody InquiryRequest request) {
        Inquiry updatedInquiry = InquiryFormRequest(request);
        Inquiry updated = inquiryService.updateInquiry(inquiryId, updatedInquiry, request.getPassword());
        return ResponseEntity.ok(updated);
    }


    /**
     * 문의글 삭제
     * @param inquiryId // 삭제하려는 문의
     * @param password //헤더에
     * @return
     */
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long inquiryId, @RequestHeader("password") String password) {
        inquiryService.deleteInquiry(inquiryId, password);
        return ResponseEntity.ok().build();
    }



}
