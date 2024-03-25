package PU.pushop.product.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductCategory;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.ProductSize;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.entity.enums.Size;
import PU.pushop.product.model.ProductCategoryDto;
import PU.pushop.product.model.ProductDto;
import PU.pushop.product.service.ProductServiceV1;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductApiControllerV1 {

    private final ProductServiceV1 productServiceV1;

    // Request Data
    @Data
    static class ProductRequest {
        private String productName;
        private ProductType productType;
        private Integer price;
        private String productInfo;
        private String manufacturer;
    }

    private Product ProductFromRequest(ProductRequest request) {
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        product.setProductInfo(request.getProductInfo());
        product.setManufacturer(request.getManufacturer());
        return product;
    }

    // Response Data
    @Data
    private class ProductResponse {
        private Long productId;
        private String productName;
        private Integer price;

        public ProductResponse(Long productId, String productName, Integer price) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
        }
    }

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
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = ProductFromRequest(request);

        Long createProductId = productServiceV1.createProduct(product);

        ProductResponse response = new ProductResponse(createProductId, product.getProductName(), product.getPrice());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest request) {
        Product updatedProduct = ProductFromRequest(request);
        Product updated = productServiceV1.updateProduct(productId, updatedProduct);
        ProductResponse response = new ProductResponse(updated.getProductId(), updated.getProductName(), updated.getPrice());

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * 상품 정보 삭제
     *
     * @param productId
     * @return
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productServiceV1.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }


    /**
     * 카테고리
     */

    // 전체 카테고리 조회 - 자식 카테고리가 한번 더 나오는 문제 때문에 findall 사용하면 안 될 듯
    /*@GetMapping("/categorys")
    public ResponseEntity<?> getCategoryList() {
        return ResponseEntity.ok(productServiceV1.getCategoryList());
    }*/

    @GetMapping("/categorys")
    public ResponseEntity<?> getCategoryList() {
        List<ProductCategory> topLevelCategories = productServiceV1.getTopLevelCategories(); // 최상위 부모 카테고리만 가져오는 메서드

        // 부모 카테고리 리스트를 DTO로 변환
        List<ProductCategoryDto> categoryDtoList = topLevelCategories.stream()
                .map(ProductCategoryDto::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryDtoList);
    }

    // 부모 카테고리 생성
    @PostMapping("/categorys/parent")
    public ResponseEntity<Long> createParentCategory(@RequestBody ProductCategory category) {
        Long categoryId = productServiceV1.createCategory(category, null); // 부모 카테고리 생성 시 parentId를 null로 전달
        return ResponseEntity.ok(categoryId);
    }

    // 1. 부모 카테고리 id를 URL로 보냄
    @PostMapping("/categorys/child/{parentId}")
    public ResponseEntity<Long> createChildCategory(@RequestBody ProductCategory category, @PathVariable Long parentId) {
        Long categoryId = productServiceV1.createCategory(category, parentId); // 부모 카테고리의 ID를 parentId로 전달하여 자식 카테고리 생성
        return ResponseEntity.ok(categoryId);
    }

    // 2. 부모 카테고리 id를 바디로 보냄
    @PostMapping("/categorys/child")
    public ResponseEntity<Long> createChildCategory(@RequestBody ProductCategory category) {
        Long parentId = category.getParent().getCategoryId(); // 요청 바디에 있는 부모 카테고리의 ID를 가져옵니다.
        Long categoryId = productServiceV1.createCategory(category, parentId); // 부모 카테고리의 ID를 parentId로 전달하여 자식 카테고리 생성
        return ResponseEntity.ok(categoryId);
    }

    // 3. 부모 카테고리 이름을 바디로 보냄






    /**
     * 색상 등록
     */
    @Data
    static class ColorRequest {
        private String color;
    }

    private ProductColor ColorFormRequest(ColorRequest request) {
        ProductColor productColor = new ProductColor();
        productColor.setColor(request.getColor());
        return productColor;
    }

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
     * 사이즈 생성
     */
    @Data
    static class SizeRequest {
        private Size size;
    }

    private ProductSize SizeFormRequest(SizeRequest request) {
        ProductSize productSize = new ProductSize();
        productSize.setSize(request.getSize());
        return productSize;
    }

    @PostMapping("/size/new")
    public ResponseEntity<?> createSize(@Valid @RequestBody SizeRequest request) {
        ProductSize size = SizeFormRequest(request);

        try {
            Long createdSizeId = productServiceV1.createSize(size);
            return ResponseEntity.status(HttpStatus.CREATED).body("사이즈 등록 완료" + createdSizeId);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("중복된 사이즈입니다.");
        }
    }
    // 사이즈는 XSMALL, SMALL, MEDIUM, LARGE, XLARGE, FREE 을 enum으로 받아오고 있는데, 다른 것들(카테고리, 컬러)와 달리 더 추가될 것이 없다고 판단되므로 아예 product 또는 인벤토리 테이블에서 enum 컬럼으로 사용하는 것이 어떨지 의논이 필요함.
}
