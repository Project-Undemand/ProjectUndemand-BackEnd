package PU.pushop.product.entity;

import PU.pushop.payment.entity.PaymentHistory;
import PU.pushop.product.entity.enums.ProductType;
import PU.pushop.wishList.entity.WishList;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "products_table")
public class Product {
    @Id
    @SequenceGenerator(
            name = "product_sequence",
            sequenceName = "product_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_sequence"
    )
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "is_sale")
    private Boolean isSale = false;

    @Column(name = "is_recomment")
    private Boolean isRecommend = false;

    @OneToMany(mappedBy = "product")
    private List<WishList> wishLists;

    @Column(name = "wishlist_count")
    private Long wishListCount;

    @OneToMany(mappedBy = "product")
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setWishLists(List<WishList> wishLists) {
        this. wishLists = wishLists;
    }

    public void setIsSale(Boolean isSale) {
        this.isSale = isSale;
    }

    public void setIsRecommend(Boolean isRecommend) {
        this.isRecommend = isRecommend;
    }

    public Product() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
