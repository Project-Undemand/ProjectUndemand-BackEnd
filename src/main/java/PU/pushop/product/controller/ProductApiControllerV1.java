package PU.pushop.product.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.product.model.*;
import PU.pushop.product.service.ProductServiceV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ProductApiControllerV1 {

    private final ProductServiceV1 productServiceV1;

    /**
     * 전체 상품 조회
     *
     * @return
     */
/*    @GetMapping("/products")
    public ResponseEntity<List<ProductListDto>> productList() {
        List<ProductListDto> productList = productServiceV1.allProducts();
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }*/

    /**
     * 전체 상품 조회 (페이징 처리)
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductListDto>> productList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductListDto> productPage = productServiceV1.allProductsPaged(page, size);
        return new ResponseEntity<>(productPage.getContent(), HttpStatus.OK);
    }

    /**
     * 최신순
     * @return
     */
    /*@GetMapping("/products/new")
    public ResponseEntity<List<ProductListDto>> NewProductList() {
        List<ProductListDto> productList = productServiceV1.getNewProducts();
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }*/

    /**
     * 최신순(페이징 처리)
     * @return
     */
    @GetMapping("/products/new")
    public ResponseEntity<List<ProductListDto>> newProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductListDto> newProductPage = productServiceV1.getNewProductsPaged(page,size);
        return new ResponseEntity<>(newProductPage.getContent(), HttpStatus.OK);
    }


    /**
     * 인기순
     * @return
     */
   /* @GetMapping("/products/best")
    public ResponseEntity<List<ProductListDto>> bestProductList() {
        List<ProductListDto> productList = productServiceV1.getBestProducts();
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }*/

    /**
     * 인기순 (페이징 처리)
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/products/best")
    public ResponseEntity<List<ProductListDto>> bestProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductListDto> newProductPage = productServiceV1.getBestProductsPaged(page,size);
        return new ResponseEntity<>(newProductPage.getContent(), HttpStatus.OK);
    }

    /**
     * 할인중 목록 (페이징 처리)
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/products/discount")
    public ResponseEntity<List<ProductListDto>> discountProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductListDto> newProductPage = productServiceV1.getDiscountProductsPaged(page,size);
        return new ResponseEntity<>(newProductPage.getContent(), HttpStatus.OK);
    }

    /**
     * 추천 상품 목록 (페이징 처리)
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/products/recommend")
    public ResponseEntity<List<ProductListDto>> recommendProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductListDto> newProductPage = productServiceV1.getRecommendProductsPaged(page,size);
        return new ResponseEntity<>(newProductPage.getContent(), HttpStatus.OK);
    }





    /**
     * 상품 등록
     *
     * @param requestDto
     * @return productId, productName, price
     */
    @PostMapping("/products/new")
    public ResponseEntity<String> createProduct(@Valid @RequestBody ProductCreateDto requestDto) {
        ProductResponseDto response = new ProductResponseDto(productServiceV1.createProduct(requestDto));
        return ResponseEntity.status(HttpStatus.CREATED).body("상품 등록 완료. : " + response.getProductName());
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
