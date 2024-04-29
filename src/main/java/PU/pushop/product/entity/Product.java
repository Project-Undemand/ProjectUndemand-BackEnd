package PU.pushop.product.entity;

import PU.pushop.contentImgs.entity.ContentImages;
import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.product.model.ProductCreateDto;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productThumbnail.entity.ProductThumbnail;
import PU.pushop.reviewImg.ReviewImg;
import PU.pushop.wishList.entity.WishList;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products_table")
public class Product {
//    @SequenceGenerator(
//            name = "product_sequence",
//            sequenceName = "product_sequence",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "product_sequence"
//    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

    @Column(nullable = false, name = "product_name")
    @NotBlank(message = "상품 이름은 필수입니다.")
    private String productName;

    @Column(name = "price", nullable = false, columnDefinition = "INT CHECK (price >= 0)")
    private Integer price;

    @Column(name = "product_info")
    private String productInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String manufacturer;

    @Column(name = "is_discount")
    private Boolean isDiscount = false;

    @Column(name = "discount_rate", nullable = true)
    private Integer discountRate;

    @Column(name = "is_recommend")
    private Boolean isRecommend = false;

    @OneToMany(mappedBy = "product")
    private List<WishList> wishLists;

    @Column(name = "wishlist_count")
    private Long wishListCount;

    @OneToMany(mappedBy = "product")
    private List<ProductManagement> productManagements = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductThumbnail> productThumbnails = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContentImages> contentImages = new ArrayList<>();

    public void setWishListCount(Long wishListCount) {
        this.wishListCount = wishListCount;
    }


    public Product() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructs a new Product object .
     */
    public Product(
            String productName,
            ProductType productType,
            int price, String productInfo,
            String manufacturer,
            boolean isDiscount,
            boolean isRecommend) {
        this.productName = productName;
        this.productType = productType;
        this.price = price;
        this.productInfo = productInfo;
        this.manufacturer = manufacturer;
        this.isDiscount = isDiscount;
        this.isRecommend = isRecommend;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 상품 생성 메서드에서 사용하는 생성자
    public Product(ProductCreateDto productCreateDto) {
        this.productName = productCreateDto.getProductName();
        this.productType = productCreateDto.getProductType();
        this.price = productCreateDto.getPrice();
        this.productInfo = productCreateDto.getProductInfo();
        this.manufacturer = productCreateDto.getManufacturer();
        this.isDiscount = productCreateDto.getIsDiscount();
        this.discountRate = Boolean.TRUE.equals(productCreateDto.getIsDiscount()) ? productCreateDto.getDiscountRate() : null;
        this.isRecommend = productCreateDto.getIsRecommend();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProduct(ProductCreateDto productCreateDto) {
        this.productName = productCreateDto.getProductName();
        this.productType = productCreateDto.getProductType();
        this.price = productCreateDto.getPrice();
        this.productInfo = productCreateDto.getProductInfo();
        this.manufacturer = productCreateDto.getManufacturer();
        this.isDiscount = productCreateDto.getIsDiscount();
        this.discountRate = Boolean.TRUE.equals(productCreateDto.getIsDiscount()) ? productCreateDto.getDiscountRate() : null;
        this.isRecommend = productCreateDto.getIsRecommend();
        this.updatedAt = LocalDateTime.now();
    }

    // 상품 더미데이터 생성
    public static Product createDummyProduct(String productName,
                                             ProductType productType,
                                             int price, String productInfo,
                                             String manufacturer,
                                             boolean isDiscount,
                                             boolean isRecommend) {
        return new Product(productName, productType, price, productInfo, manufacturer, isDiscount, isRecommend);
    }

    public Product(Long productId) {
        this.productId = productId;
    }

    public static Product createProductById(Long productId) {
        return new Product(productId);
    }
}
