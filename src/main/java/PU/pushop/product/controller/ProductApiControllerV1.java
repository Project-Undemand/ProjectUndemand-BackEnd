package PU.pushop.product.controller;

import PU.pushop.global.queries.Condition;
import PU.pushop.global.queries.OrderBy;
import PU.pushop.product.entity.Product;
import PU.pushop.product.model.*;
import PU.pushop.product.service.ProductOrderService;
import PU.pushop.product.service.ProductServiceV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ProductApiControllerV1 {

    public final ModelMapper modelMapper;
    private final ProductServiceV1 productServiceV1;
    private final ProductOrderService productOrderService;


    /**
     * 상품 목록 (카테고리/조건별 필터링, 조건별 정렬, 검색 통합)
     * @param page
     * @param size
     * @param condition
     * @param category
     * @param order
     * @param keyword
     * @return
     */
    @GetMapping("/products")
    public Page<ProductListDto> getFilteredAndSortedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Condition condition,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) OrderBy order,
            @RequestParam(required = false) String keyword
    ) {
        return productOrderService.getFilteredAndSortedProducts(page, size, condition, order, category, keyword);
    }

    /**
     * 상품 등록
     * @param requestDto
     * @return productId, productName, price
     */
    @PostMapping("/products/new")
//    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> createProduct(@Valid @RequestParam(value = "thumbnail_images", required = false) List<MultipartFile> thumbnailImgs, @RequestParam(value = "content_images", required = false) List<MultipartFile> contentImgs, @ModelAttribute ProductCreateDto requestDto) {
        Long productId = productServiceV1.createProduct(requestDto, thumbnailImgs,contentImgs); // 저장한 상품의 pk

        return ResponseEntity.status(HttpStatus.CREATED).body("상품 등록 완료. Id : " + productId);
    }

    /**
     * 상품 상세 정보
     *
     * @param productId
     * @return
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable Long productId) {
        ProductDetailDto productDetail = productServiceV1.productDetail(productId);
        return new ResponseEntity<>(productDetail, HttpStatus.OK);
    }

    // 랭킹순으로 상품 리스트를 반환하는 엔드포인트
    @GetMapping("/products/ranking")
    public List<ProductRankResponseDto> getTopProducts(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        return productServiceV1.getProductListByRanking(limit);
    }

    /**
     * 상품 정보 수정
     *
     * @param productId
     * @param request
     * @return
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductCreateDto request) {
        // 상품 정보 업데이트
        Product updated = productServiceV1.updateProduct(productId, request);

        ProductResponseDto response = new ProductResponseDto(updated);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 상품 정보 삭제
     * @param productId
     * @return
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        productServiceV1.deleteProduct(productId);
        return ResponseEntity.ok().body(DELETE_SUCCESS);
    }

    /**
     * 색상 등록
     * @param request
     * @return
     */
    @PostMapping("/color/new")
    public ResponseEntity<String> createColor(@Valid @RequestBody ProductColorDto request) {

        try {
            Long createdColorId = productServiceV1.createColor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("색상 등록 완료 " + createdColorId);
        } catch (DataIntegrityViolationException e) {
            // 중복된 이름에 대한 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("중복된 이름으로 색상을 등록할 수 없습니다.");
        }
    }

    /**
     * 색상 삭제
     * @param colorId
     * @return
     */
    @DeleteMapping("/color/{colorId}")
    public ResponseEntity<String> deleteColor(@PathVariable Long colorId) {
        productServiceV1.deleteColor(colorId);

        return ResponseEntity.ok().body(DELETE_SUCCESS);
    }
}
