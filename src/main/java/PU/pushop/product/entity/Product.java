package PU.pushop.product.entity;

import PU.pushop.product.entity.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "products")
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

    @Column(name = "product_id")
    private Long productId;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column(nullable = false)
    private String productName;

    @Column(name = "price")
    private Integer price;

    @Column(name = "product_info")
    private String productInfo;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_DATE ON UPDATE CURRENT_DATE")
    private LocalDate updatedAt;

    @Column(nullable = false)
    private String manufacturer;

    private Boolean isSale = false;

    private Boolean isRecommend = false;

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

    public void setIsSale(Boolean isSale) {
        this.isSale = isSale;
    }

    public void setIsRecommend(Boolean isRecommend) {
        this.isRecommend = isRecommend;
    }

    public Product() {

        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();

    }

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDate.now();
    }

}
