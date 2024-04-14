package PU.pushop.category.service;


import PU.pushop.category.entity.Category;
import PU.pushop.category.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;



class CategoryServiceV1Test {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceV1 categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("전체 카테고리 조회 시 최상위 부모 카테고리 반환")
    public void testGetTopLevelCategories() {
        // Given
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        Mockito.when(categoryRepository.findByParentIsNull()).thenReturn(Collections.singletonList(parentCategory));

        // When
        List<Category> topLevelCategories = categoryService.getTopLevelCategories();

        // Then
        Assertions.assertEquals(1, topLevelCategories.size());
        Assertions.assertEquals("Parent Category", topLevelCategories.get(0).getName());
    }

    @Test
    @DisplayName("부모 카테고리 생성 성공")
    public void testCreateParentCategory() {
        // Given
        Category category = new Category();
        category.setName("New Parent Category");
        Long generatedCategoryId = 1L;
        Mockito.when(categoryRepository.save(Mockito.any())).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0); // 저장되는 카테고리 객체 가져오기
            savedCategory.setCategoryId(generatedCategoryId); // categoryId 설정
            return savedCategory; // 저장된 카테고리 객체 반환
        });

        // When
        Long categoryId = categoryService.createCategory(category, null);

        // Then
        Assertions.assertEquals(generatedCategoryId, categoryId);
    }

    @Test
    @DisplayName("자식 카테고리 생성 성공")
    public void testCreateChildCategory() {
        // Given
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        parentCategory.setDepth(1L);
        Mockito.when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(parentCategory));

        Long generatedChildCategoryId = 2L;
        Mockito.when(categoryRepository.save(Mockito.any())).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0); // 저장되는 카테고리 객체 가져오기
            savedCategory.setCategoryId(generatedChildCategoryId); // categoryId 설정
            return savedCategory; // 저장된 카테고리 객체 반환
        });

        Category childCategory = new Category();
        childCategory.setName("Child Category");

        // When
        Long childCategoryId = categoryService.createCategory(childCategory, 1L);

        // Then
        Assertions.assertNotNull(childCategoryId);
        Assertions.assertEquals(generatedChildCategoryId, childCategoryId);
        Assertions.assertEquals(parentCategory, childCategory.getParent());
    }

    @Test
    @DisplayName("상위 카테고리를 삭제할 때 하위 카테고리가 있는 경우 IllegalArgumentException 발생")
    public void testDeleteCategoryWithChildren() {
        // Given
        Category parentCategory = new Category();
        parentCategory.setName("Parent Category");
        Mockito.when(categoryRepository.findByCategoryId(Mockito.any())).thenReturn(Optional.of(parentCategory));
        parentCategory.setChildren(Collections.singletonList(new Category()));

        // When & Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    @DisplayName("상위 카테고리를 삭제할 때 하위 카테고리가 없는 경우 삭제")
    public void testDeleteCategoryWithoutChildren() {
        // Given
        Category category = new Category();
        category.setName("Category");
        Mockito.when(categoryRepository.findByCategoryId(Mockito.any())).thenReturn(Optional.of(category));

        // When
        categoryService.deleteCategory(1L);

        // Then
        Mockito.verify(categoryRepository, Mockito.times(1)).delete(Mockito.any());
    }

}