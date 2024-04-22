package PU.pushop.global.dummydata.util;

import PU.pushop.category.entity.Category;
import PU.pushop.category.repository.CategoryRepository;
import PU.pushop.category.service.CategoryServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class CategoryDataUtil {

    private final CategoryRepository categoryRepository;
    private final CategoryServiceV1 categoryService;

    public void generateCategoryData() {


        // "상의"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> topsChildren = new ArrayList<>();
        Category topParentCategory = createAndSaveParentCategory(1L, "상의", 0L, new ArrayList<>());
        topsChildren.add(createAndSaveChildCCategoryV2(2L, "후드", topParentCategory.getCategoryId(),  new ArrayList<>()));
        topsChildren.add(createAndSaveChildCCategoryV2(3L, "맨투맨", topParentCategory.getCategoryId(), new ArrayList<>()));
        topsChildren.add(createAndSaveChildCCategoryV2(4L, "반팔 셔츠", topParentCategory.getCategoryId(),new ArrayList<>()));
        topsChildren.add(createAndSaveChildCCategoryV2(5L, "긴팔 셔츠", topParentCategory.getCategoryId(),new ArrayList<>()));
        topsChildren.add(createAndSaveChildCCategoryV2(6L, "반팔티", topParentCategory.getCategoryId(),new ArrayList<>()));
        topsChildren.add(createAndSaveChildCCategoryV2(7L, "긴팔티", topParentCategory.getCategoryId(),new ArrayList<>()));
        topsChildren.add(createAndSaveChildCCategoryV2(8L, "니트/스웨터", topParentCategory.getCategoryId(),new ArrayList<>()));
        topsChildren.add(createAndSaveChildCCategoryV2(9L, "블라우스", topParentCategory.getCategoryId(),new ArrayList<>()));

        topParentCategory.setChildren(topsChildren);

        // "하의"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> bottomChildren = new ArrayList<>();
        Category bottomParentCategory = createAndSaveParentCategory(10L, "하의", 0L, new ArrayList<>());
        bottomChildren.add(createAndSaveChildCCategoryV2(11L, "긴바지", bottomParentCategory.getCategoryId(), new ArrayList<>()));
        bottomChildren.add(createAndSaveChildCCategoryV2(12L, "반바지", bottomParentCategory.getCategoryId(), new ArrayList<>()));
        bottomChildren.add(createAndSaveChildCCategoryV2(13L, "치마", bottomParentCategory.getCategoryId(), new ArrayList<>()));

        bottomParentCategory.setChildren(bottomChildren);



        // "dress&set"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> dressChildren = new ArrayList<>();
        Category dressParentCategory = createAndSaveParentCategory(14L, "dress&set", 0L, new ArrayList<>());
        dressChildren.add(createAndSaveChildCCategoryV2(15L, "원피스", dressParentCategory.getCategoryId(), new ArrayList<>()));
        dressChildren.add(createAndSaveChildCCategoryV2(16L, "투피스", dressParentCategory.getCategoryId(), new ArrayList<>()));
        dressChildren.add(createAndSaveChildCCategoryV2(17L, "셋업", dressParentCategory.getCategoryId(), new ArrayList<>()));

        dressParentCategory.setChildren(dressChildren);

        // "아우터"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> outerChildren = new ArrayList<>();
        Category outerParentCategory = createAndSaveParentCategory(18L, "아우터", 0L, new ArrayList<>());
        outerChildren.add(createAndSaveChildCCategoryV2(19L, "숏패딩", outerParentCategory.getCategoryId(), new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(20L, "롱패딩", outerParentCategory.getCategoryId(), new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(21L, "가디건", outerParentCategory.getCategoryId(), new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(22L, "재킷", outerParentCategory.getCategoryId(), new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(23L, "코트", outerParentCategory.getCategoryId(), new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(24L, "무스탕", outerParentCategory.getCategoryId(), new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(25L, "조끼", outerParentCategory.getCategoryId(), new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(26L, "경량패딩", outerParentCategory.getCategoryId(), new ArrayList<>()));

        outerParentCategory.setChildren(outerChildren);

        // "신발"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> shoesChildren = new ArrayList<>();
        Category shoesParentCategory = createAndSaveParentCategory(27L, "신발", 0L, new ArrayList<>());
        shoesChildren.add(createAndSaveChildCCategoryV2(28L, "스니커즈", shoesParentCategory.getCategoryId(), new ArrayList<>()));
        shoesChildren.add(createAndSaveChildCCategoryV2(29L, "샌들/슬리퍼", shoesParentCategory.getCategoryId(), new ArrayList<>()));
        shoesChildren.add(createAndSaveChildCCategoryV2(30L, "부츠", shoesParentCategory.getCategoryId(), new ArrayList<>()));

        shoesParentCategory.setChildren(shoesChildren);

        // "악세서리"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> accessoryChildren = new ArrayList<>();
        Category accessoryParentCategory = createAndSaveParentCategory(31L, "악세사리", 0L, new ArrayList<>());
        accessoryChildren.add(createAndSaveChildCCategoryV2(32L, "모자", accessoryParentCategory.getCategoryId(), new ArrayList<>()));
        accessoryChildren.add(createAndSaveChildCCategoryV2(33L, "양말", accessoryParentCategory.getCategoryId(), new ArrayList<>()));
        accessoryChildren.add(createAndSaveChildCCategoryV2(34L, "가방", accessoryParentCategory.getCategoryId(), new ArrayList<>()));

        accessoryParentCategory.setChildren(accessoryChildren);
    }


    private Category createAndSaveParentCategory(Long categoryId, String name, Long depth, List<Category> children) {
        // DB 에 이미 존재하는 category Id 인지 확인한다.
        Optional<Category> optionalCategory = categoryRepository.findByCategoryId(categoryId);

        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            categoryService.createCategory(category, null);
            return category;
        } else {
            Category category = Category.createCategoryDummyData(categoryId, name, depth, children);
            children.forEach(child -> child.setParent(category));
            category.setChildren(children);
            categoryService.createCategory(category, null);
            return category;
        }
    }

    protected Category createAndSaveChildCCategoryV2(Long categoryId, String name, Long parentId, List<Category> children) {
        Category category = Category.createChildCategory(categoryId, name, 1L, children);

        if (parentId != null) {
            Category parentCategory = categoryRepository.findById(parentId).orElseThrow(() ->
                    new RuntimeException("부모 카테고리를 찾을 수 없습니다. ID: " + parentId));
            category.setParent(parentCategory);
        }

        categoryService.createCategory(category, parentId);
        return category;
    }


    /**
     * 2024.04.23 김성우
     * 부모 카테고리 생성 후, 자식 카테고리를 생성하지 않아
     * 부모 카테고리 id를 찾을 수 없어 RuntimeException 발생
     */
    public void generateCategoryDataV1() {
        // "상의"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> TopChildren = new ArrayList<>();
        TopChildren.add(createAndSaveChildCCategoryV2(2L, "후드", 1L, new ArrayList<>()));
        TopChildren.add(createAndSaveChildCCategoryV2(3L, "맨투맨", 1L,new ArrayList<>()));
        TopChildren.add(createAndSaveChildCCategoryV2(4L, "반팔 셔츠", 1L,new ArrayList<>()));
        TopChildren.add(createAndSaveChildCCategoryV2(5L, "긴팔 셔츠", 1L,new ArrayList<>()));
        TopChildren.add(createAndSaveChildCCategoryV2(6L, "반팔티", 1L,new ArrayList<>()));
        TopChildren.add(createAndSaveChildCCategoryV2(7L, "긴팔티", 1L,new ArrayList<>()));
        TopChildren.add(createAndSaveChildCCategoryV2(8L, "니트/스웨터", 1L,new ArrayList<>()));
        TopChildren.add(createAndSaveChildCCategoryV2(9L, "블라우스", 1L,new ArrayList<>()));
        createAndSaveParentCategory(1L, "상의", 0L, TopChildren);

        // "하의"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> bottomChildren = new ArrayList<>();
        bottomChildren.add(createAndSaveChildCCategoryV2(11L, "긴바지", 10L, new ArrayList<>()));
        bottomChildren.add(createAndSaveChildCCategoryV2(12L, "반바지", 10L, new ArrayList<>()));
        bottomChildren.add(createAndSaveChildCCategoryV2(13L, "치마", 10L, new ArrayList<>()));
        createAndSaveParentCategory(10L, "하의", 0L, bottomChildren);

        // "dress&set"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> setChildren = new ArrayList<>();
        setChildren.add(createAndSaveChildCCategoryV2(15L, "원피스", 14L, new ArrayList<>()));
        setChildren.add(createAndSaveChildCCategoryV2(16L, "투피스", 14L, new ArrayList<>()));
        setChildren.add(createAndSaveChildCCategoryV2(17L, "셋업", 14L, new ArrayList<>()));
        createAndSaveParentCategory(14L, "dress&set", 0L, setChildren);

        // "아우터"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> outerChildren = new ArrayList<>();
        outerChildren.add(createAndSaveChildCCategoryV2(19L, "숏패딩", 18L, new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(20L, "롱패딩", 18L, new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(21L, "가디건", 18L, new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(22L, "재킷", 18L, new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(23L, "코트", 18L, new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(24L, "무스탕", 18L, new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(25L, "조끼", 18L, new ArrayList<>()));
        outerChildren.add(createAndSaveChildCCategoryV2(26L, "경량패딩", 18L, new ArrayList<>()));
        createAndSaveParentCategory(18L, "아우터", 0L, outerChildren);

        // "신발"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> shoesChildren = new ArrayList<>();
        shoesChildren.add(createAndSaveChildCCategoryV2(28L, "스니커즈", 27L, new ArrayList<>()));
        shoesChildren.add(createAndSaveChildCCategoryV2(29L, "샌들/슬리퍼", 27L, new ArrayList<>()));
        shoesChildren.add(createAndSaveChildCCategoryV2(30L, "부츠", 27L, new ArrayList<>()));
        createAndSaveParentCategory(27L, "신발", 0L, shoesChildren);

        // "악세서리"에 대한 하위 카테고리 생성 후 , 하위 카테고리로 다음을 추가
        List<Category> accessoryChildren = new ArrayList<>();
        accessoryChildren.add(createAndSaveChildCCategoryV2(32L, "모자", 31L, new ArrayList<>()));
        accessoryChildren.add(createAndSaveChildCCategoryV2(33L, "양말", 31L, new ArrayList<>()));
        accessoryChildren.add(createAndSaveChildCCategoryV2(34L, "가방", 31L, new ArrayList<>()));
        createAndSaveParentCategory(31L, "악세사리", 0L, accessoryChildren);
    }
}
