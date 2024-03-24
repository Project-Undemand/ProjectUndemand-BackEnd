package PU.pushop.product.entity;

import PU.pushop.product.entity.enums.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_size")
public class ProductSize {
    @Id
    @SequenceGenerator(
            name = "size_sequence",
            sequenceName = "size_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "size_sequence"
    )
    @Column(name = "size_id", unique = true)
    private Long sizeId;

    @Column(name = "size_value")
    @Enumerated(EnumType.STRING)
    private Size size;

}
