package PU.pushop.product.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.product.model.ProductDto;
import PU.pushop.product.model.ProductResponseDto;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.product.service.ProductServiceV1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

class ProductApiControllerV1Test {

    @Mock
    private ProductServiceV1 productServiceV1;

    @InjectMocks
    private ProductApiControllerV1 productApiControllerV1;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test@DisplayName("상품 등록 성공")
    void testCreateProduct() {
        // Given
        ProductCreateDto request = new ProductCreateDto();
        request.setProductName("Test Product");
        request.setPrice(1000);
        request.setProductType(ProductType.WOMAN);

        Product product = new Product();
        product.setProductId(1L);
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setProductType(request.getProductType());

        when(productServiceV1.createProduct(any())).thenReturn(product);

        // When
        ResponseEntity<?> responseEntity = productApiControllerV1.createProduct(request);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("상품 등록 완료. : " + request.getProductName(), responseEntity.getBody());
    }

    @Test
    @DisplayName("가격을 음수로 설정했을 때 IllegalArgumentException 발생")
    void testCreateProductWithNegativePrice() {
        // Given
        ProductCreateDto request = new ProductCreateDto();
        request.setProductName("Test Product");
        request.setPrice(-100); // 음수 가격
        request.setProductType(ProductType.WOMAN);

        when(productServiceV1.createProduct(any())).thenThrow(new IllegalArgumentException("가격은 0 이상이어야 합니다."));

        // When & Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> productApiControllerV1.createProduct(request));

    }


    @Test@DisplayName("Id로 상품 찾기")
    void testGetProductById() {
        // Given
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Test Product");
        product.setPrice(1000);
        product.setProductType(ProductType.WOMAN);

        when(productServiceV1.productDetail(productId)).thenReturn(product);

        // When
        ResponseEntity<ProductDto> responseEntity = productApiControllerV1.getProductById(productId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(productId, responseEntity.getBody().getProductId());
    }

    @Test@DisplayName("상품 수정")
    public void testUpdateProduct() {
        // Given
        Long productId = 1L;
        ProductCreateDto request = new ProductCreateDto();
        request.setProductName("Updated Product");
        request.setPrice(2000);
        request.setProductType(ProductType.MAN);

        Product updatedProduct = new Product();
        updatedProduct.setProductId(productId);
        updatedProduct.setProductName(request.getProductName());
        updatedProduct.setPrice(request.getPrice());
        updatedProduct.setProductType(request.getProductType());

        when(productServiceV1.updateProduct(eq(productId), any())).thenReturn(updatedProduct);

        // When
        ResponseEntity<?> responseEntity = productApiControllerV1.updateProduct(productId, request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedProduct.getProductName(), ((ProductResponseDto) responseEntity.getBody()).getProductName());
        assertEquals(updatedProduct.getPrice(), ((ProductResponseDto) responseEntity.getBody()).getPrice());
    }

    @Test@DisplayName("상품 삭제")
    public void testDeleteProduct() {
        // Given
        Long productId = 1L;

        // When
        ResponseEntity<?> responseEntity = productApiControllerV1.deleteProduct(productId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(productServiceV1, times(1)).deleteProduct(productId);
    }

    @Test@DisplayName("색상 등록")
    public void testCreateColor() {
        // Given
        String colorName = "Red";
        ProductColor color = new ProductColor();
        color.setColorId(1L);
        color.setColor(colorName);

        when(productServiceV1.createColor(any())).thenReturn(1L);

        // When
        ResponseEntity<?> responseEntity = productApiControllerV1.createColor(new ProductApiControllerV1.ColorRequest(colorName));

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("색상 등록 완료 1", responseEntity.getBody());
    }

    @Test
    @DisplayName("중복된 이름의 색상 등록 시 Internal Server Error 반환")
    void createColor_DuplicateName_ReturnsInternalServerError() {
        // Given
        ProductApiControllerV1.ColorRequest request = new ProductApiControllerV1.ColorRequest();
        request.setColor("Red"); // 이미 존재하는 색상 이름

        ProductColor existingColor = new ProductColor();
        existingColor.setColor("Red");

        ProductApiControllerV1 productService;
        when(productServiceV1.createColor(any())).thenThrow(DataIntegrityViolationException.class);

        // When
        ResponseEntity<?> response = productApiControllerV1.createColor(request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("중복된 이름으로 색상을 등록할 수 없습니다.", response.getBody());
    }

    @Test@DisplayName("색상 삭제")
    public void testDeleteColor() {
        // Given
        Long colorId = 1L;

        // When
        ResponseEntity<?> responseEntity = productApiControllerV1.deleteColor(colorId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(productServiceV1, times(1)).deleteColor(colorId);
    }
}