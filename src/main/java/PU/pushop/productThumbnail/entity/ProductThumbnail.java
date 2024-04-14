package PU.pushop.productThumbnail.entity;

import PU.pushop.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_thumbnails")
public class ProductThumbnail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thumbnail_id")
    private Long thumbnailId;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 생성자 추가
    public ProductThumbnail(Product product, String imagePath) {
        this.product = product;
        this.imagePath = imagePath;
    }

    public ProductThumbnail() {

    }

    public void updateItemImg(String imagePath){
        this.imagePath = imagePath;
    }
}