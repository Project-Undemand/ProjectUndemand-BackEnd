package PU.pushop.Inquiry.controller;

import PU.pushop.Inquiry.model.InquiryCreateDto;
import PU.pushop.Inquiry.model.InquiryUpdateDto;
import PU.pushop.global.authentication.jwts.utils.JWTUtil;
import PU.pushop.members.entity.Member;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.members.repository.MemberRepositoryV1;
import PU.pushop.Inquiry.entity.Inquiry;
import PU.pushop.Inquiry.model.InquiryDto;
import PU.pushop.Inquiry.service.InquiryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import PU.pushop.global.authentication.jwts.login.CustomUserDetails;
import PU.pushop.global.authentication.oauth2.custom.entity.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;
    private final MemberRepositoryV1 memberRepository;
    private final JWTUtil jwtUtil;


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
     * @param requestDto
     * @param productId
     * @return
     */
    @PostMapping("/new/{productId}")
    public ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryCreateDto requestDto, @PathVariable Long productId , HttpServletRequest request) {
        try {

            Long createdId = inquiryService.createInquiry(requestDto, productId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body("문의 등록 완료. Id : "+createdId);
        } catch (HttpMessageNotReadableException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 문의 유형입니다.");

        }

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
     * @param requestDto
     * @return
     */
    @PutMapping("/{inquiryId}")
    public ResponseEntity<?> updateInquiry(@PathVariable Long inquiryId, @Valid @RequestBody InquiryUpdateDto requestDto) {
        Inquiry updated = inquiryService.updateInquiry(inquiryId, requestDto, requestDto.getPassword());
        return ResponseEntity.ok("수정 완료"+ updated.getInquiryId());
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
