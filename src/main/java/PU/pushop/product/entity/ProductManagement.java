package PU.pushop.product.entity;

import PU.pushop.product.entity.enums.Size;
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
            name = "color_sequence",
            sequenceName = "color_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "color_sequence"
    )
    @Column(name = "inventory_id")
    private Long inventoryId; // ProductManagement 테이블의 pk

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name = "color_id")
    private ProductColor color;

    @OneToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Column(name = "initail_stock")
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
