package PU.pushop.category.service;

import PU.pushop.category.entity.Category;
import PU.pushop.category.model.CategoryDto;
import PU.pushop.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CategoryServiceV1 {
    public final CategoryRepository categoryRepository;

    /**
     * 전체 카테고리 조회
     * @return
     */
    public List<Category> getTopLevelCategories() {
        return categoryRepository.findByParentIsNull(); // 부모 카테고리가 없는 경우를 조회하여 최상위 부모 카테고리를 반환
    }


    public List<CategoryDto> getCategoryList() {
        List<CategoryDto> results = categoryRepository.findAll().stream().map(CategoryDto::of).collect(Collectors.toList());
        return results;
    }

    /**
     * 카테고리 생성
     * parentId 파라미터가 없는 경우 - 부모 카테고리를 만든다.
     * 있는 경우 - 해당하는 부모 카테고리 밑에 자식 카테고리를 만든다.
     * @param category
     * @param parentId
     * @return
     */
    public Long createCategory(Category category, Long parentId){
        if (parentId != null) {
            // 부모 카테고리가 지정된 경우
            Category parentCategory = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new NoSuchElementException("해당 카테고리를 찾을 수 없습니다."));

            category.setParent(parentCategory);
            category.setDepth(parentCategory.getDepth() + 1); // 자식 카테고리의 depth를 설정
            parentCategory.getChildren().add(category);
        } else {
            // 부모 카테고리가 지정되지 않은 경우 최상위 카테고리로 설정
            category.setDepth(0L);
        }

        Category savedCategory = categoryRepository.save(category);
        return savedCategory.getCategoryId();
    }

    /**
     * 카테고리 삭제
     * @param categoryId
     */
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new NoSuchElementException("해당 카테고리를 찾을 수 없습니다. Id : " + categoryId));

        if (category.getChildren().isEmpty()) {
            categoryRepository.delete(category);
        } else {
            throw new IllegalArgumentException("삭제 실패 : 하위 카테고리가 존재합니다.");
        }
    }


}
