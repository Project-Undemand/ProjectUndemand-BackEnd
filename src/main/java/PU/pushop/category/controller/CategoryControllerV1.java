package PU.pushop.category.controller;

import PU.pushop.category.entity.Category;
import PU.pushop.category.model.CategoryDto;
import PU.pushop.category.service.CategoryServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static PU.pushop.global.ResponseMessageConstants.DELETE_SUCCESS;

@RestController
@RequestMapping("/api/v1/categorys")
@RequiredArgsConstructor
public class CategoryControllerV1 {

    private final CategoryServiceV1 categoryServiceV1;

    // 전체 카테고리 조회 - 자식 카테고리가 한번 더 나오는 문제 때문에 findall 사용하면 안 될 듯
    /*@GetMapping("/categorys")
    public ResponseEntity<?> getCategoryList() {
        return ResponseEntity.ok(productServiceV1.getCategoryList());
    }*/

    /**
     * 전체 카테고리 조회
     * @return
     */
    @GetMapping("")
    public ResponseEntity<List<CategoryDto>> getCategoryList() {
        List<Category> topLevelCategories = categoryServiceV1.getTopLevelCategories(); // 최상위 부모 카테고리만 가져오는 메서드

        // 부모 카테고리 리스트를 DTO로 변환
        List<CategoryDto> categoryDtoList = topLevelCategories.stream()
                .map(CategoryDto::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryDtoList);
    }

    /**
     * 부모 카테고리 생성
     * @param category
     * @return
     */
    @PostMapping("/parent")
    public ResponseEntity<Long> createParentCategory(@RequestBody Category category) {
        Long categoryId = categoryServiceV1.createCategory(category, null); // 부모 카테고리 생성 시 parentId를 null로 전달
        return ResponseEntity.ok(categoryId);
    }

    /**
     * 자식 카테고리 생성
     * @param category
     * @param parentId
     * @return
     */
    @PostMapping("/child/{parentId}")
    public ResponseEntity<Long> createChildCategory(@RequestBody Category category, @PathVariable Long parentId) {
        Long categoryId = categoryServiceV1.createCategory(category, parentId); // 부모 카테고리의 ID를 parentId로 전달하여 자식 카테고리 생성
        return ResponseEntity.ok(categoryId);
    }
/*

    // 2. 부모 카테고리 id를 바디로 보냄
    @PostMapping("/child")
    public ResponseEntity<Long> createChildCategory(@RequestBody Category category) {
        Long parentId = category.getParent().getCategoryId(); // 요청 바디에 있는 부모 카테고리의 ID를 가져옵니다.
        Long categoryId = categoryServiceV1.createCategory(category, parentId); // 부모 카테고리의 ID를 parentId로 전달하여 자식 카테고리 생성
        return ResponseEntity.ok(categoryId);
    }
*/

    /**
     * 카테고리 삭제
     * @param categoryId
     * @return
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryServiceV1.deleteCategory(categoryId);
        return ResponseEntity.ok(DELETE_SUCCESS);
    }

}
