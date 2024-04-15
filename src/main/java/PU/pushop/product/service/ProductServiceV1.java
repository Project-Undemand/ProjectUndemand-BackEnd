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

    /**
     * 전체 상품 리스트 - 페이징 처리된 상품 찾기
     */
    public Page<ProductListDto> allProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepositoryV1.findAll(pageable);
        return productPage.map(ProductListDto::new); // Spring Data JPA에서 제공하는 Page 객체의 메소드. 페이지 내의 각 엔티티를 다른 형식으로 매핑
    }

    /**
     * 최신 상품
     * @return
     */
    public List<ProductListDto> getNewProducts() {
        List<Product> products = productRepositoryV1.findAllByOrderByCreatedAtDesc();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductListDto.class))
                .toList();
    }

    /**
     * 생성일자 기준 리스트 페이징
     * @param page
     * @param size
     * @return
     */
    public Page<ProductListDto> getNewProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productPagingRepository.findAllByOrderByCreatedAtDesc(pageable);
        return productPage.map(ProductListDto::new);

    }


    /**
     * 위시리스트 개수를 기준으로 내림차순으로 상품 리스트 가져오기
     */
    public List<ProductListDto> getBestProducts() {
        List<Product> products = productRepositoryV1.findAllByOrderByWishListCountDesc();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductListDto.class))
                .toList();
    }

    /**
     * 위시리스트 기준 리스트 페이징
     * @param page
     * @param size
     * @return
     */
    public Page<ProductListDto> getBestProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productPagingRepository.findAllByOrderByWishListCountDesc(pageable);
        return productPage.map(ProductListDto::new);

    }

    /**
     * 할인 상품 목록 페이징
     * @param page
     * @param size
     * @return
     */
    public Page<ProductListDto> getDiscountProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productPagingRepository.findByIsDiscountTrue(pageable);
        return productPage.map(ProductListDto::new);

    }

    /**
     * 추천 상품 목록 페이징
     * @param page
     * @param size
     * @return
     */
    public Page<ProductListDto> getRecommendProductsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productPagingRepository.findByIsRecommendTrue(pageable);
        return productPage.map(ProductListDto::new);

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
