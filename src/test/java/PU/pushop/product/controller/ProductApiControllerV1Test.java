package PU.pushop.product.controller;

import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.product.model.ProductDto;
import PU.pushop.product.model.ProductResponseDto;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.product.service.ProductServiceV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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

    @Test
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
    void testCreateProductWithNegativePrice() {
        // Given
        ProductCreateDto request = new ProductCreateDto();
        request.setProductName("Test Product");
        request.setPrice(-100); // 음수 가격
        request.setProductType(ProductType.WOMAN);

        // When ProductServiceV1의 createProduct 메서드가 호출될 때 음수 가격이 들어있는 ProductCreateDto가 전달되면
        // IllegalArgumentException을 발생시키도록 설정
        when(productServiceV1.createProduct(any())).thenThrow(new IllegalArgumentException("가격은 0 이상이어야 합니다."));

        // When
        ResponseEntity<?> responseEntity = productApiControllerV1.createProduct(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("가격은 0 이상이어야 합니다.", responseEntity.getBody());
    }


    @Test
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

    @Test
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

    @Test
    public void testDeleteProduct() {
        // Given
        Long productId = 1L;

        // When
        ResponseEntity<?> responseEntity = productApiControllerV1.deleteProduct(productId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(productServiceV1, times(1)).deleteProduct(productId);
    }

    @Test
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