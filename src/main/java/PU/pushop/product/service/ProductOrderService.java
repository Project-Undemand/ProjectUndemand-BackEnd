package PU.pushop.product.service;

import PU.pushop.global.queries.Condition;
import PU.pushop.global.queries.OrderBy;
import PU.pushop.global.queries.ProductQueryHelper;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.QProduct;
import PU.pushop.product.model.ProductListDto;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static PU.pushop.product.entity.QProduct.product;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductOrderService {

    public final ProductRepositoryV1 productRepository;
    public final ProductColorRepository productColorRepository;
    public final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;

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
                .fetch().size();
        // querydsl 개발진 측에서 fetchCount와 groupby를 함께 사용할 때 생기는 문제로 인해 fetchcount 함수를 deprecated 시켰다고함
//                .fetchCount();

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


}
