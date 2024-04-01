package PU.pushop.product.entity;

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

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "product_id")
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