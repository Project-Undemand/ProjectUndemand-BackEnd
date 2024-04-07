package PU.pushop.category.service;

import PU.pushop.category.entity.Category;
import PU.pushop.category.model.CategoryDto;
import PU.pushop.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceV1 {
    public final CategoryRepository categoryRepository;

    // 전체 카테고리
    public List<Category> getTopLevelCategories() {
        return categoryRepository.findByParentIsNull(); // 부모 카테고리가 없는 경우를 조회하여 최상위 부모 카테고리를 반환
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CategoryDto> getCategoryList() {
        List<CategoryDto> results = categoryRepository.findAll().stream().map(CategoryDto::of).collect(Collectors.toList());
        return results;
    }

    @Transactional
    public Long createCategory(Category category, Long parentId){
        if (parentId != null) {
            // 부모 카테고리가 지정된 경우
            Optional<Category> parentOptional = categoryRepository.findById(parentId);
            /*if (parentOptional.isEmpty()) {
                // 부모 카테고리가 존재하지 않는 경우 예외 처리
                throw new ChangeSetPersister.NotFoundException();
            }*/

            Category parentCategory = parentOptional.get();
            category.setParent(parentCategory);
            category.setDepth(parentCategory.getDepth() + 1); // 자식 카테고리의 depth를 설정합니다.
            parentCategory.getChildren().add(category);
        } else {
            // 부모 카테고리가 지정되지 않은 경우 최상위 카테고리로 설정
            category.setDepth(0L);
        }

        Category savedCategory = categoryRepository.save(category);
        return savedCategory.getCategoryId();
    }
    /*@Transactional
    public Long createCategory(ProductCategory category) {
        categoryRepository.save(category);
        return category.getCategoryId();
    }*/
}
