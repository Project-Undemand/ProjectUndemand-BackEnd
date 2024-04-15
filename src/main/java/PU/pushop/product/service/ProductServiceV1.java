package PU.pushop.product.service;


import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.model.ProductColorDto;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.product.model.ProductDetailDto;
import PU.pushop.product.model.ProductListDto;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductPagingRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static PU.pushop.global.ResponseMessageConstants.PRODUCT_NOT_FOUND;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProductServiceV1 {
    public final ProductRepositoryV1 productRepositoryV1;
    public final ProductColorRepository productColorRepository;
    public final ModelMapper modelMapper;
    public final ProductPagingRepository productPagingRepository;


    /**
     * 상품 등록
     *
     * @param requestDto
     * @return productId
     */
    public Product createProduct(ProductCreateDto requestDto) {
        if (requestDto.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        // DTO를 엔티티로 매핑
        Product product = modelMapper.map(requestDto, Product.class);
        productRepositoryV1.save(product);
        return product;
    }

    /**
     * 상품 상세정보 - 상품 하나 찾기
     *
     * @param productId
     * @return
     */
    public ProductDetailDto productDetail(Long productId) {
        Product product = productRepositoryV1.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));
        return modelMapper.map(product, ProductDetailDto.class);
    }

    /**
     * 전체 상품 리스트 - 전체 상품 찾기
     */
    public List<ProductListDto> allProducts() {
        List<Product> products = productRepositoryV1.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductListDto.class))
                .toList();
    }

    // 페이징 처리, 상품 목록 매핑 메서드
    public Page<ProductListDto> getProductsPaged(Pageable pageable, Function<Pageable, Page<Product>> pageFetcher) {
        return pageFetcher.apply(pageable).map(ProductListDto::new);
    }

    // 최신순
    public Page<ProductListDto> getNewProductsPaged(Pageable pageable) {
        return getProductsPaged(pageable, productPagingRepository::findAllByOrderByCreatedAtDesc);

    }
    // 인기순
    public Page<ProductListDto> getBestProductsPaged(Pageable pageable) {
        return getProductsPaged(pageable, productPagingRepository::findAllByOrderByWishListCountDesc);
    }

    // 할인 상품
    public Page<ProductListDto> getDiscountProductsPaged(Pageable pageable) {
        return getProductsPaged(pageable, p -> productPagingRepository.findByIsDiscountTrue(pageable));
    }

    // 추천 상품
    public Page<ProductListDto> getRecommendProductsPaged(Pageable pageable) {
        return getProductsPaged(pageable, p -> productPagingRepository.findByIsRecommendTrue(pageable));
    }



    /**
     * 상품 정보 수정
     *
     * @param productId
     * @param updatedDto
     * @return
     */
    public Product updateProduct(Long productId, ProductCreateDto updatedDto) {
        Product existingProduct = productRepositoryV1.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        // ModelMapper를 사용하여 DTO에서 엔티티로 매핑
        modelMapper.map(updatedDto, existingProduct);

        // 수정된 상품 정보 저장 후 return
        return productRepositoryV1.save(existingProduct);
    }

    /**
     * 상품 삭제
     *
     * @param productId
     */
    public void deleteProduct(Long productId) {
        Product existingProduct = productRepositoryV1.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(PRODUCT_NOT_FOUND));

        productRepositoryV1.delete(existingProduct);
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


}
