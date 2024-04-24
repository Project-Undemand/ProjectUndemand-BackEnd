package PU.pushop.productManagement.entity;

import PU.pushop.order.entity.Orders;
import PU.pushop.product.entity.Product;
import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.productManagement.entity.enums.Size;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_management")
public class ProductManagement {
    @Id
    @SequenceGenerator(
            name = "product_management_sequence",
            sequenceName = "product_management_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_management_sequence"
    )
    @Column(name = "inventory_id" )
    private Long inventoryId; // ProductManagement 테이블의 pk

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "color_id", unique = false, nullable = false)
    private ProductColor color;

    @ManyToOne
    @JoinColumn(name = "category_id", unique = false, nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private Size size;

    @Column(name = "initial_stock")
    private Long initialStock;

    @Column(name = "additional_stock")
    private Long additionalStock;

    @Column(name = "product_stock")
    private Long productStock;

    private boolean isSoldOut = false;

    private boolean isRestockAvailable = false;

    private boolean isRestocked = false;

    @ManyToMany(mappedBy = "productManagements")
    private List<Orders> orders = new ArrayList<>();

    public ProductManagement(Product product, ProductColor color, Category category, Size size, Long initialStock, Long initialStock1, Boolean isRestockAvailable, Boolean isRestocked, Boolean isSoldOut) {
        this.product = product;
        this.color = color;
        this.category = category;
        this.size = size;
        this.initialStock = initialStock;
        this.productStock = initialStock1;
    }

    public ProductManagement(Long initialStock, Long additionalStock, Category categoryById, Product productById, Long productStock, Size size, ProductColor color, boolean isRestockAvailable, boolean isRestocked, boolean isSoldOut) {
        this.initialStock = initialStock;
        this.additionalStock = additionalStock;
        this.category = categoryById;
        this.product = productById;
        this.productStock = productStock;
        this.isRestockAvailable = isRestockAvailable;
        this.isRestocked = isRestocked;
        this.isSoldOut = isSoldOut;
        this.size = size;
        this.color = color;
    }

    public void updateInventory(Long additionalStock, Long productStock, Boolean isRestockAvailable, Boolean isRestocked, Boolean isSoldOut) {
        this.additionalStock = additionalStock;
        this.productStock = productStock;
        this.isRestockAvailable = isRestockAvailable;
        this.isRestocked = isRestocked;
        this.isSoldOut = isSoldOut;
    }




}
