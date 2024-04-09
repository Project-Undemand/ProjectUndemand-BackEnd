package PU.pushop.product.service;


import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.product.repository.ProductColorRepository;
import PU.pushop.product.repository.ProductRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceV1 {
    public final ProductRepositoryV1 productRepositoryV1;

    public final ProductColorRepository productColorRepository;

/*    public Product findProductById(Long productId) {
        return productRepositoryV1.findByProductId(productId)
                .orElse(null); // productId에 해당하는 Product가 없을 경우 null 반환
    }*/

    /**
     * 상품 등록
     * @param product
     * @return productId
     */
    @Transactional
    public Long createProduct(Product product) {
        productRepositoryV1.save(product);
        return product.getProductId();
    }

    /**
     * 상품 상세정보 - 상품 하나 찾기
     * @param productId
     * @return
     */
    public Product productDetail(Long productId) {
        return productRepositoryV1.findById(productId).get();
    }

    /**
     * 전체 상품 리스트 - 전체 상품 찾기
     */
    public List<Product> allProducts() {
        return productRepositoryV1.findAll();
    }

    /**
     * 상품 정보 수정
     * @param productId
     * @param updatedProduct
     * @return
     */
    public Product updateProduct(Long productId, Product updatedProduct) {
        Product existingProduct = productRepositoryV1.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 기존 상품 정보 업데이트
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setProductType(updatedProduct.getProductType());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setProductInfo(updatedProduct.getProductInfo());
        existingProduct.setManufacturer(updatedProduct.getManufacturer());

        // 수정된 상품 정보 저장 후 return
        return productRepositoryV1.save(existingProduct);

    }

    /**
     * 상품 삭제
     * @param productId
     */
    public void deleteProduct(Long productId) {
        Product existingProduct = productRepositoryV1.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        productRepositoryV1.delete(existingProduct);
    }

    /**
     * 색상 등록
     * @param color
     * @return
     */
    @Transactional
    public Long createColor(ProductColor color) {
        productColorRepository.save(color);
        return color.getColorId();
    }

}
