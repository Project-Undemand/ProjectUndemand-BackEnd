package PU.pushop.product.service;


import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.model.ProductColorDto;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.product.model.ProductDetailDto;
import PU.pushop.product.model.ProductListDto;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productThumbnail.service.ProductThumbnailServiceV1;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

import static PU.pushop.global.ResponseMessageConstants.PRODUCT_NOT_FOUND;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProductServiceV1 {
    public final ProductRepositoryV1 productRepository;
    public final ProductColorRepository productColorRepository;
    public final ModelMapper modelMapper;
    private final ProductThumbnailServiceV1 productThumbnailService;


    /**
     * 상품 등록
     *
     * @param requestDto
     * @return productId
     */
    public Long createProduct(ProductCreateDto requestDto, List<MultipartFile> images) {
        if (requestDto.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        // DTO를 엔티티로 매핑
        Product product = modelMapper.map(requestDto, Product.class);
        productRepository.save(product);
        // 추가 - 썸네일 저장 메서드 실행
        productThumbnailService.uploadThumbnail(product, images);
        return product.getProductId();
    }

    /**
     * 상품 상세정보 - 상품 하나 찾기
     *
     * @param productId
     * @return
     */
    public ProductDetailDto productDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));
        return modelMapper.map(product, ProductDetailDto.class);
    }

    /**
     * 전체 상품 리스트 - 전체 상품 찾기
     */
    public List<ProductListDto> allProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductListDto.class))
                .toList();
    }


    /**
     * 조건별 상품 리스트 페이징
     * @param pageable
     * @param condition
     * @return
     */
    public Page<ProductListDto> getProductsByConditionPaged(Pageable pageable, String condition) {
        switch (condition) {
            case "new":
                return productRepository.findAllByOrderByCreatedAtDesc(pageable).map(ProductListDto::new);
            case "best":
                return productRepository.findAllByOrderByWishListCountDesc(pageable).map(ProductListDto::new);
            case "discount":
                return productRepository.findByIsDiscountTrue(pageable).map(ProductListDto::new);
            case "recommend":
                return productRepository.findByIsRecommendTrue(pageable).map(ProductListDto::new);
            default:
                throw new IllegalArgumentException("Invalid condition: " + condition);
        }
    }

    /**
     * 상품 정보 수정
     *
     * @param productId
     * @param updatedDto
     * @return
     */
    public Product updateProduct(Long productId, ProductCreateDto updatedDto) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        // ModelMapper를 사용하여 DTO에서 엔티티로 매핑
        modelMapper.map(updatedDto, existingProduct);

        // 수정된 상품 정보 저장 후 return
        return productRepository.save(existingProduct);
    }

    /**
     * 상품 삭제
     *
     * @param productId
     */
    public void deleteProduct(Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        productRepository.delete(existingProduct);
    }

    /**
     * 색상 등록
     *
     * @param request
     * @return
     */
    public Long createColor(ProductColorDto request) {
        ProductColor color = modelMapper.map(request, ProductColor.class);
        productColorRepository.save(color);
        return color.getColorId();
    }

    /**
     * 색상 삭제
     *
     * @param colorId
     */
    public void deleteColor(Long colorId) {
        ProductColor color = productColorRepository.findById(colorId)
                .orElseThrow(() -> new NoSuchElementException("해당 색상을 찾을 수 없습니다. Id : " + colorId));
        productColorRepository.delete(color);
    }

    public Page<Product> searchProducts(String keyword) {
        return productRepository.findByProductNameContainingOrProductInfoContaining(keyword, keyword);
    }


}
