package PU.pushop.InventoryProduct.entity;


import PU.pushop.category.entity.Category;
import PU.pushop.product.entity.Product;
import PU.pushop.product.entity.ProductColor;
import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.entity.enums.Size;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inventory_product")
public class InventoryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_product_id" )
    private Long inventoryProductId;

    @OneToOne(mappedBy = "inventoryProduct", cascade = CascadeType.ALL)
    private ProductManagement productManagement;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "color_id", unique = false, nullable = false)
    private ProductColor color;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "category_id", unique = false, nullable = false)
    private Category category;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private Size size;

    @NonNull
    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode;

    @NonNull
    @Column(name = "erp_product_name", nullable = false)
    private String erpProductName;

    @Column(name = "initial_stock")
    private Long initialStock;

    @Setter
    @Column(name = "product_stock")
    private Long productStock;

    @Setter
    private boolean isSoldOut = false;

    @Setter
    private boolean isRestockAvailable = false;

    @Setter
    private boolean isRestocked = false;

    @OneToMany(mappedBy = "inventoryProduct")
    private List<StockMovement> stockMovementList = new ArrayList<>();

    public InventoryProduct(@NonNull Product product, @NonNull ProductColor color, @NonNull Category category, @NonNull Size size, @NonNull String productCode, @NonNull String erpProductName, Long initialStock, Long productStock, boolean isSoldOut, boolean isRestockAvailable, boolean isRestocked) {
        this.product = product;
        this.color = color;
        this.category = category;
        this.size = size;
        this.productCode = productCode;
        this.erpProductName = erpProductName;
        this.initialStock = initialStock;
        this.productStock = productStock;
        this.isSoldOut = isSoldOut;
        this.isRestockAvailable = isRestockAvailable;
        this.isRestocked = isRestocked;
    }

    // productStock 수정 메서드
    public void updateProductStock(Long productStock) {
        this.productStock = productStock;
    }
}
