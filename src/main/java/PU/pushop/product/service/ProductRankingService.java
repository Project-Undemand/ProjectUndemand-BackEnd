package PU.pushop.product.service;

import PU.pushop.product.entity.Product;
import PU.pushop.product.model.ProductRankResponseDto;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductRankingService {

    public final ProductRepositoryV1 productRepository;
    public final ProductColorRepository productColorRepository;
    public final ModelMapper modelMapper;
    private final RedisTemplate<String, String> redisTemplate;

    // 상품 조회수 증가 메서드
    public void increaseProductViews(Long productId) {
        String key = "product_views";
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), 1);
    }

    // 랭킹을 위한 상품 조회수 가져오는 메서드
    public Set<String> getTopProductIds(int limit) {
        String key = "product_views";

        return redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
    }

    // 랭킹순으로 상품 리스트를 조회하는 메서드
    public List<ProductRankResponseDto> getProductListByRanking(int limit) {

        Set<String> productIds = getTopProductIds(limit);
        List<Long> productIdList = productIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Product> products = productIdList.stream()
                .map(productId -> productRepository.findByProductId(productId).orElse(null))
                .toList();

        return products.stream()
                .map(product -> { // Product -> ProductRankResponseDto 변환
                    ProductRankResponseDto ProductRankResponseDto = modelMapper.map(product, ProductRankResponseDto.class);
                    // ProductThumbnail의 imagePath를 매핑
                    ProductRankResponseDto.setProductThumbnails(
                            product.getProductThumbnails().get(0).getImagePath());
                    return ProductRankResponseDto;
                })
                .toList();
    }



}
