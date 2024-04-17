//package PU.pushop.Inquiry.service;
//
//import PU.pushop.Inquiry.entity.Inquiry;
//import PU.pushop.Inquiry.entity.enums.InquiryType;
//import PU.pushop.Inquiry.model.InquiryCreateDto;
//import PU.pushop.Inquiry.model.InquiryDto;
//import PU.pushop.Inquiry.repository.InquiryRepository;
//import PU.pushop.product.entity.Product;
//import PU.pushop.product.repository.ProductRepositoryV1;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//
//class InquiryServiceTest {
//    @Mock
//    private InquiryRepository inquiryRepository;
//
//    @Mock
//    private ProductRepositoryV1 productRepository;
//
//    @InjectMocks
//    private InquiryService inquiryService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("문의글 작성")
//    public void testCreateInquiry() {
//
//        // Given
//
//        InquiryCreateDto request = new InquiryCreateDto();
//        request.setName("John Doe");
//        request.setEmail("test@example.com");
//        request.setInquiryType(InquiryType.DELIVERY);
//        request.setInquiryTitle("문의 제목");
//        request.setInquiryContent("문의 내용");
//        request.setPassword("password");
//
//        Long productId = 1L;
//        Product product = new Product();
//        when(productRepository.findByProductId(anyLong())).thenReturn(Optional.of(product));
//
//        // When
//
//        Long inquiryId = inquiryService.createInquiry(request, productId);
//
//        // Then
//
//        verify(inquiryRepository, times(1)).save(any(Inquiry.class));
//
//    }
//    @Test
//    @DisplayName("필수 필드가 빠졌을 때 실패")
//    public void testCreateInquiryWithMissingFields() {
//        // Given
//
//        InquiryCreateDto request = new InquiryCreateDto();
//        request.setMemberId(1L);
//
//        Long productId = 1L;
//
//        // When & Then
//
//        assertThrows(NullPointerException.class, () -> inquiryService.createInquiry(request, productId));
//    }
//    @Test
//    @DisplayName("문의글 수정")
//    public void testUpdateInquiry() {
//        InquiryCreateDto updatedInquiry = new InquiryCreateDto();
//        updatedInquiry.setInquiryType(InquiryType.DELIVERY);
//        updatedInquiry.setInquiryTitle("문의 제목");
//        updatedInquiry.setInquiryContent("문의 내용");
//
//        Inquiry existingInquiry = new Inquiry();
//        existingInquiry.setInquiryId(1L);
//        existingInquiry.setName("작성자");
//        existingInquiry.setEmail("test@example.com");
//        existingInquiry.setInquiryType(InquiryType.DELIVERY);
//        existingInquiry.setInquiryTitle("문의 제목");
//        existingInquiry.setInquiryContent("문의 내용");
//        existingInquiry.setPassword("password");
//
//        when(inquiryRepository.findById(anyLong())).thenReturn(Optional.of(existingInquiry));
//        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(existingInquiry);
//
//        Inquiry updated = inquiryService.updateInquiry(1L, updatedInquiry, "password");
//
//        assertNotNull(updated);
//    }
//
//    @Test
//    @DisplayName("문의글 삭제")
//    public void testDeleteInquiry() {
//        Inquiry existingInquiry = new Inquiry();
//        existingInquiry.setInquiryId(1L);
//        existingInquiry.setName("작성자");
//        existingInquiry.setEmail("test@example.com");
//        existingInquiry.setInquiryType(InquiryType.DELIVERY);
//        existingInquiry.setInquiryTitle("문의 제목");
//        existingInquiry.setInquiryContent("문의 내용");
//        existingInquiry.setPassword("password");
//
//        when(inquiryRepository.findById(anyLong())).thenReturn(Optional.of(existingInquiry));
//
//        inquiryService.deleteInquiry(1L, "password");
//
//        verify(inquiryRepository, times(1)).delete(existingInquiry);
//    }
//
//    @Test
//    @DisplayName("전체 문의 보기")
//    public void testAllInquiryList() {
//        List<Inquiry> inquiries = new ArrayList<>(); // Add some inquiries
//        when(inquiryRepository.findAll()).thenReturn(inquiries);
//
//        List<InquiryDto> inquiryDtoList = inquiryService.allInquiryList();
//
//        assertNotNull(inquiryDtoList);
//    }
//
//    @Test
//    @DisplayName("문의 수정 시 비밀번호 틀림")
//    public void testUpdateInquiryWithWrongPassword() {
//        InquiryCreateDto updatedInquiry = new InquiryCreateDto();
//
//        Inquiry existingInquiry = new Inquiry();
//        existingInquiry.setInquiryId(1L);
//        existingInquiry.setPassword("correctPassword");
//
//        when(inquiryRepository.findById(anyLong())).thenReturn(Optional.of(existingInquiry));
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            inquiryService.updateInquiry(1L, updatedInquiry, "wrongPassword");
//        });
//    }
//
//    @Test
//    @DisplayName("문의 삭제 시 비밀번호 틀림")
//    public void testDeleteInquiryWithWrongPassword() {
//        Inquiry existingInquiry = new Inquiry();
//        existingInquiry.setInquiryId(1L);
//        existingInquiry.setPassword("correctPassword");
//
//        when(inquiryRepository.findById(anyLong())).thenReturn(Optional.of(existingInquiry));
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            inquiryService.deleteInquiry(1L, "wrongPassword");
//        });
//
//    }
//
//}