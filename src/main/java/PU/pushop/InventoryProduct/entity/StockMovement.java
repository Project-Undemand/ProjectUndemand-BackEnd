package PU.pushop.InventoryProduct.entity;


import PU.pushop.InventoryProduct.entity.enums.MovementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "stock_movement")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_product_id", nullable = false)
    private InventoryProduct inventoryProduct;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    private String stockContent;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;
}
