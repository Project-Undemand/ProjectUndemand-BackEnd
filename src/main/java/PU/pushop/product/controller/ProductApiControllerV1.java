package PU.pushop.product.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.product.model.ProductDto;
import PU.pushop.product.model.ProductResponseDto;
import PU.pushop.product.service.ProductServiceV1;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> productList() {
        List<Product> productList = productServiceV1.allProducts();
        List<ProductDto> collect = productList.stream()
                .map(ProductDto::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(collect, HttpStatus.OK);
    }

    /**
     * 상품 등록
     *
     * @param request
     * @return productId, productName, price (테스트용)
     */
    @PostMapping("/products/new")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateDto request) {
        Product product = ProductCreateDto.requestForm(request);
        ProductResponseDto response = new ProductResponseDto(productServiceV1.createProduct(product));

        return ResponseEntity.status(HttpStatus.CREATED).body("상품 등록 완료. : " + response.getProductName());
    }

    /**
     * 상품 상세 정보
     *
     * @param productId
     * @return
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
        Product productDetail = productServiceV1.productDetail(productId);
        ProductDto productDto = new ProductDto(productDetail);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    /**
     * 상품 정보 수정
     *
     * @param productId
     * @param request
     * @return
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductCreateDto request) {
        Product updatedProduct = ProductCreateDto.requestForm(request);
        Product updated = productServiceV1.updateProduct(productId, updatedProduct);

        ProductResponseDto response = new ProductResponseDto(updated);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * 상품 정보 삭제
     * @param productId
     * @return
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productServiceV1.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @Data
    static class ColorRequest {
        private String color;
    }

    /**
     * 색상 등록
     * @param request
     * @return
     */
    private ProductColor ColorFormRequest(ColorRequest request) {
        ProductColor productColor = new ProductColor();
        productColor.setColor(request.getColor());
        return productColor;
    }

    /**
     * 색상 등록
     * @param request
     * @return
     */
    @PostMapping("/color/new")
    public ResponseEntity<?> createColor(@Valid @RequestBody ColorRequest request) {
        ProductColor color = ColorFormRequest(request);

        try {
            Long createdColorId = productServiceV1.createColor(color);
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
    public ResponseEntity<?> deleteColor(@PathVariable Long colorId) {
        productServiceV1.deleteColor(colorId);

        return ResponseEntity.ok().build();
    }

}
