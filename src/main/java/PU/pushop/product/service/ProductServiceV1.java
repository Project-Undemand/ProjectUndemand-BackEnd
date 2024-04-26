package PU.pushop.product.service;


import PU.pushop.category.entity.Category;
import PU.pushop.global.queries.Condition;
import PU.pushop.global.queries.OrderBy;
import PU.pushop.global.queries.ProductQueryHelper;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.QProduct;
import PU.pushop.product.model.ProductColorDto;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.product.model.ProductDetailDto;
import PU.pushop.product.model.ProductListDto;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import PU.pushop.productThumbnail.service.ProductThumbnailServiceV1;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

import static PU.pushop.global.ResponseMessageConstants.PRODUCT_NOT_FOUND;
import static PU.pushop.product.entity.QProduct.product;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceV1 {
    public final ProductRepositoryV1 productRepository;
    public final ProductColorRepository productColorRepository;
    public final ModelMapper modelMapper;
    private final ProductThumbnailServiceV1 productThumbnailService;

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;



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
//        Product product = modelMapper.map(requestDto, Product.class);
        Product product = new Product(requestDto);
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
     * 필터링 및 정렬
     * @param page
     * @param size
     * @param condition
     * @param order
     * @return
     */
    public Page<ProductListDto> getFilteredAndSortedProducts(int page, int size, Condition condition, OrderBy order, Long category, String keyword) {
        // 필터링
        BooleanBuilder filterBuilder = ProductQueryHelper.createFilterBuilder(condition, category, keyword, QProduct.product);

        // 정렬
        OrderSpecifier<?> orderSpecifier = ProductQueryHelper.getOrderSpecifier(order, product);

        // 필터링 및 정렬 적용
        List<Product> results = getFilteredAndSortedResults(orderSpecifier, filterBuilder, page, size);

        // 전체 카운트 조회 쿼리
        long totalCount = queryFactory.selectFrom(product)
                .where(filterBuilder)
                .fetchCount();

        // ProductListDto로 변환
        List<ProductListDto> productList = mapToProductListDto(results);

        /*List<ProductListDto> productList = results.stream()
                .map(ProductListDto::new)
                .collect(Collectors.toList());
*/

        return new PageImpl<>(productList, PageRequest.of(page, size), totalCount);
    }

    // 필터링 및 정렬 수행하는 메서드
    private List<Product> getFilteredAndSortedResults(OrderSpecifier orderSpecifier, BooleanBuilder filterBuilder, int page, int size) {
        return queryFactory.selectFrom(product)
                .leftJoin(product.productThumbnails).fetchJoin()
                .where(filterBuilder)
                .orderBy(orderSpecifier)
                .offset(page * size)
                .limit(size)
                .fetch();
    }

    // Product 리스트 -> ProductListDto 리스트로 변환 메서드
    private List<ProductListDto> mapToProductListDto(List<Product> results) {
        return results.stream()
                .map(product -> { // Product -> ProductListDto 변환
                    ProductListDto productListDto = modelMapper.map(product, ProductListDto.class);
                    // ProductThumbnail의 imagePath를 매핑
                    productListDto.setProductThumbnails(
                            product.getProductThumbnails().stream()
                                    .map(ProductThumbnail::getImagePath)
                                    .toList()
                    );
                    return productListDto;
                })
                .toList();
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

        existingProduct.updateProduct(updatedDto);
        // ModelMapper를 사용하여 DTO에서 엔티티로 매핑
//        modelMapper.map(updatedDto, existingProduct);

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




}
