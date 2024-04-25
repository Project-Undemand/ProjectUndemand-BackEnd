package PU.pushop.product.service;


import PU.pushop.global.queries.ProductQueryHelper;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.model.*;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productThumbnail.service.ProductThumbnailServiceV1;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
     * 검색 및 정렬
     * @param keyword
     * @param sortBy
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public List<ProductListDto> searchAndFilterProducts(String keyword, String sortBy, int pageNumber, int pageSize) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        // 검색 조건
        BooleanBuilder searchConditions = new BooleanBuilder();
        if (keyword != null) {
            searchConditions.or(product.productName.contains(keyword))
                    .or(product.productInfo.contains(keyword));
        }

        // 정렬 및 페이징
        List<Product> productList= queryFactory.selectFrom(product)
                .where(searchConditions)
                .orderBy(ProductQueryHelper.getOrderSpecifier(sortBy, product))
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .fetch();

        return productList.stream()
                .map(ProductListDto::new) // Product를 ProductListDto로 매핑
                .collect(Collectors.toList());
    }

    /**
     * 필터링 및 정렬
     * @param page
     * @param size
     * @param condition
     * @param order
     * @return
     */
    public Page<ProductListDto> getFilteredAndSortedProducts(int page, int size, String condition, String order, Long category) {
        BooleanBuilder predicate = createPredicate(condition, category);

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(order);

        List<Product> results = queryFactory.selectFrom(product)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(page * size)
                .limit(size)
                .fetch();

        long totalCount = queryFactory.selectFrom(product)
                .where(predicate)
                .fetchCount();

        List<ProductListDto> productList = results.stream()
                .map(ProductListDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(productList, PageRequest.of(page, size), totalCount);
    }

    private BooleanBuilder createPredicate(String condition, Long category) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (condition != null) {
            switch (condition) {
                case Conditions.NEW:
                    predicate.and(product.createdAt.after(LocalDateTime.now().minusMonths(1)));
                    break;
                case Conditions.BEST:
                    predicate.and(product.wishListCount.goe(30L));
                    break;
                case Conditions.DISCOUNT:
                    predicate.and(product.isDiscount.isTrue());
                    break;
                case Conditions.RECOMMEND:
                    predicate.and(product.isRecommend.isTrue());
                    break;
                case Conditions.MAN:
                case Conditions.WOMAN:
                case Conditions.UNISEX:
                    predicate.and(product.productType.eq(ProductType.valueOf(condition)));
                    break;
                default:
                    predicate.and(product.createdAt.after(LocalDateTime.now().minusMonths(1)));
                    break;
            }
        }

        if (category != null) {
            predicate.and(product.productManagements.any().category.categoryId.eq(category));
        }

        return predicate;
    }

    private OrderSpecifier<?> getOrderSpecifier(String order) {
        if (order == null) {
            return ProductQueryHelper.getOrderSpecifier(null, product);
        } else {
            return ProductQueryHelper.getOrderSpecifier(order, product);
        }
    }

    private static class Conditions {
        static final String NEW = "new";
        static final String BEST = "best";
        static final String DISCOUNT = "discount";
        static final String RECOMMEND = "recommend";
        static final String MAN = "MAN";
        static final String WOMAN = "WOMAN";
        static final String UNISEX = "UNISEX";

    }

    /**
     * 전체 상품 리스트 - 전체 상품 찾기
     */
/*    public List<ProductListDto> allProducts() {
        List<Product> products = productRepository.findAllWithThumbnails();

        return products.stream()
                .map(product -> {
                    ProductListDto productListDto = modelMapper.map(product, ProductListDto.class);
                    // ProductThumbnail의 imagePath를 매핑
                    productListDto.setProductThumbnails(
                            product.getProductThumbnails().stream()
                                    .map(ProductThumbnail::getImagePath)
                                    .collect(Collectors.toList())
                    );
                    return productListDto;
                })
                .toList();
    }*/

    /**
     * 조건별 상품 리스트 페이징
     * @param pageable
     * @param condition
     * @return
     */
//    public Page<ProductListDto> getProductsByConditionPaged(Pageable pageable, String condition) {
//        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
//
//        switch (condition) {
//            case "new":
//                return productRepository.findByCreatedAtAfterOrderByCreatedAtDesc(oneMonthAgo, pageable)
//                        .map(ProductListDto::new);
//            case "best":
//                return productRepository.findByWishListCountGreaterThanOrderByWishListCountDesc(30, pageable)
//                        .map(ProductListDto::new);
//            case "discount":
//                return productRepository.findByIsDiscountTrue(pageable).map(ProductListDto::new);
//            case "recommend":
//                return productRepository.findByIsRecommendTrue(pageable).map(ProductListDto::new);
//            default:
//                throw new IllegalArgumentException("Invalid condition: " + condition);
//        }
//    }


    /**
     * 상품 타입별 페이징
     * @param pageable
     * @param productType
     * @return
     */
//    public Page<ProductListDto> getProductsByTypePaged(Pageable pageable, ProductType productType) {
//        return productRepository.findByProductType(productType, pageable) // productType : enum - MAN, WOMAN,UNISEX
//                .map(ProductListDto::new);
//    }

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
