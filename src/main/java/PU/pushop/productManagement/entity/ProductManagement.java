package PU.pushop.productManagement.entity;

import PU.pushop.product.entity.Product;
import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.productManagement.entity.enums.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_management")
public class ProductManagement {
    @Id
    @SequenceGenerator(
            name = "inventory_sequence",
            sequenceName = "inventory_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "inventory_sequence"
    )
    @Column(name = "inventory_id" )
    private Long inventoryId; // ProductManagement 테이블의 pk

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "color_id", unique = false)
    private ProductColor color;

    @ManyToOne
    @JoinColumn(name = "category_id", unique = false)
    private Category category;

    @Enumerated(EnumType.STRING)
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

    public ProductManagement() {

    }
}
