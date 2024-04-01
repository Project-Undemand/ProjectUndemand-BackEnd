package PU.pushop.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_color")
public class ProductColor {
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
    @Column(name = "color_id")
    private Long colorId;

    @Column(name = "color", unique = true)
    private String color;
}
