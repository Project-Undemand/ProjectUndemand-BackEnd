package PU.pushop.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "색상 이름은 필수입니다.")
    @Column(name = "color", unique = true)
    private String color;
}
