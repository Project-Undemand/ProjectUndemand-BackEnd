package PU.pushop.category.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class  Category {
    @Id
    @SequenceGenerator(
            name = "category_sequence",
            sequenceName = "category_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "category_sequence"
    )

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Category parent;

    @Column(name = "depth")
    private Long depth;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    // ===================== 생성자 시작 =======================

    public Category() {
    }

    public Category(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Category(Long categoryId, String name, Long depth, List<Category> children) {
        this.categoryId = categoryId;
        this.name = name;
        this.depth = depth;
        this.children = children;
    }

    public Category(Category parent, Long depth, String name) {
        this.parent = parent;
        this.depth = depth;
        this.name = name;
    }

    public Category(Long depth, String name) {
        this.depth = depth;
        this.name = name;
    }

    // ===================== 생성자 끝 =======================

    public static Category createCategoryById(Long categoryId) {
        return new Category(categoryId);
    }

    public static Category createCategoryDummyData(Long categoryId, String name, Long depth, List<Category> children) {
        return new Category(categoryId, name, depth, children);
    }

    public static Category createChildCategoryDummyData(Long categoryId, String name, Long parentId, List<Category> children) {

        return null;
    }

    public static Category createChildCategory(Long categoryId, String name, Long depth, List<Category> children) {
        return new Category(categoryId, name, depth, children);
    }

/*
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }
*/

    public void setParent(Category parent) {
        this.parent = parent;
    }

/*
    public void setDepth(Long depth) {
        this.depth = depth;
    }
*/

    public void setChildren(List<Category> children) {
        this.children = children;
    }
}

