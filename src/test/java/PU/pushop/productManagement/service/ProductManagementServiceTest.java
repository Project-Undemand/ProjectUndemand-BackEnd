package PU.pushop.productManagement.service;

import PU.pushop.productManagement.entity.ProductManagement;
import PU.pushop.productManagement.repository.ProductManagementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ProductManagementServiceTest {
    @Mock
    private ProductManagementRepository productManagementRepository;

    @InjectMocks
    private ProductManagementService productManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 관리 생성")
    void createInventoryTest() {
        ProductManagement productManagement = new ProductManagement();
        productManagement.setInventoryId(1L);

        when(productManagementRepository.save(any(ProductManagement.class))).thenReturn(productManagement);

        Long inventoryId = productManagementService.createInventory(productManagement);

        verify(productManagementRepository, times(1)).save(any(ProductManagement.class));
        assertEquals(1L, inventoryId);
    }

    @Test
    @DisplayName("상품관리 상세보기")
    void inventoryDetailTest() {
        ProductManagement productManagement = new ProductManagement();
        productManagement.setInventoryId(1L);

        when(productManagementRepository.findById(1L)).thenReturn(Optional.of(productManagement));

        ProductManagement retrievedProductManagement = productManagementService.inventoryDetail(1L);

        verify(productManagementRepository, times(1)).findById(1L);
        assertEquals(1L, retrievedProductManagement.getInventoryId());
    }

    @Test
    @DisplayName("상품관리 전체보기")
    void allInventoryTest() {
        List<ProductManagement> productManagementList = new ArrayList<>();
        productManagementList.add(new ProductManagement());
        productManagementList.add(new ProductManagement());

        when(productManagementRepository.findAll()).thenReturn(productManagementList);

        List<ProductManagement> retrievedProductManagementList = productManagementService.allInventory();

        verify(productManagementRepository, times(1)).findAll();
        assertEquals(2, retrievedProductManagementList.size());
    }

    @Test
    @DisplayName("상품관리 수정")
    void updateInventoryTest() {
        ProductManagement updatedProductManagement = new ProductManagement();
        updatedProductManagement.setInventoryId(1L);

        ProductManagement existingProductManagement = new ProductManagement();
        existingProductManagement.setInventoryId(1L);

        when(productManagementRepository.findById(1L)).thenReturn(Optional.of(existingProductManagement));
        when(productManagementRepository.save(any(ProductManagement.class))).thenReturn(updatedProductManagement);

        ProductManagement result = productManagementService.updateInventory(1L, updatedProductManagement);

        verify(productManagementRepository, times(1)).findById(1L);
        verify(productManagementRepository, times(1)).save(any(ProductManagement.class));
        assertEquals(1L, result.getInventoryId());
    }

    @Test
    @DisplayName("인벤토리 삭제")
    void deleteInventoryTest() {
        ProductManagement productManagement = new ProductManagement();
        productManagement.setInventoryId(1L);

        when(productManagementRepository.findById(1L)).thenReturn(Optional.of(productManagement));

        productManagementService.deleteInventory(1L);

        verify(productManagementRepository, times(1)).delete(productManagement);
    }

    @Test
    @DisplayName("존재하지 않는 인벤토리 삭제 시도")
    void deleteNonexistentInventoryTest() {
        when(productManagementRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            productManagementService.deleteInventory(1L);
        });

        verify(productManagementRepository, never()).delete(any(ProductManagement.class));
    }

}