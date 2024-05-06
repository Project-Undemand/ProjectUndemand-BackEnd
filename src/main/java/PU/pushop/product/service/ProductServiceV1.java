package PU.pushop.product.service;


import PU.pushop.contentImgs.service.ContentImgService;
import PU.pushop.global.authorization.RequiresRole;
import PU.pushop.members.entity.enums.MemberRole;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.model.ProductColorDto;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.product.model.ProductDetailDto;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import PU.pushop.productThumbnail.service.ProductThumbnailServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static PU.pushop.global.ResponseMessageConstants.PRODUCT_NOT_FOUND;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ProductServiceV1 {
    public final ProductRepositoryV1 productRepository;
    public final ProductColorRepository productColorRepository;
    public final ModelMapper modelMapper;
    private final ProductThumbnailServiceV1 productThumbnailService;
    private final ContentImgService contentImgService;
    private final ProductRankingService productRankingService;

    /**
     * 상품 등록
     * @param requestDto
     * @return productId
     */
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
    public Long createProduct(ProductCreateDto requestDto, @Nullable List<MultipartFile> thumbnailImgs, @Nullable List<MultipartFile> contentImgs) {

        if (requestDto.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        Product product = new Product(requestDto);
        productRepository.save(product);
        // 추가 - 썸네일 저장 메서드 실행
        if (thumbnailImgs != null && !Objects.equals(thumbnailImgs.get(0).getOriginalFilename(), "")) {
            productThumbnailService.uploadThumbnail(product, thumbnailImgs);

        }
        if (contentImgs != null && !Objects.equals(contentImgs.get(0).getOriginalFilename(), "")) {
            contentImgService.uploadContentImage(product,contentImgs);

        }

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

        // 상품 조회 시 조회수 증가
        log.info("View Increment");
        productRankingService.increaseProductViews(productId);

        return modelMapper.map(product, ProductDetailDto.class);
    }




    /**
     * 상품 정보 수정
     *
     * @param productId
     * @param updatedDto
     * @return
     */
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
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
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
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
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
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
    @RequiresRole({MemberRole.ADMIN, MemberRole.SELLER})
    public void deleteColor(Long colorId) {
        ProductColor color = productColorRepository.findById(colorId)
                .orElseThrow(() -> new NoSuchElementException("해당 색상을 찾을 수 없습니다. Id : " + colorId));
        productColorRepository.delete(color);
    }



}
