//package PU.pushop.cart.service;
//
//import PU.pushop.cart.entity.Cart;
//import PU.pushop.cart.model.CartDto;
//import PU.pushop.cart.model.CartRequestDto;
//import PU.pushop.cart.repository.CartRepository;
//import PU.pushop.members.entity.Member;
//import PU.pushop.members.repository.MemberRepositoryV1;
//import PU.pushop.product.entity.Product;
//import PU.pushop.product.entity.ProductColor;
//import PU.pushop.product.repository.ProductRepositoryV1;
//import PU.pushop.productManagement.entity.ProductManagement;
//import PU.pushop.productManagement.entity.enums.Size;
//import PU.pushop.productManagement.repository.ProductManagementRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CartServiceUnitTest {
//    @Mock
//    private CartRepository cartRepository;
//    @Mock
//    private ProductRepositoryV1 productRepository;
//    @Mock
//    private MemberRepositoryV1 memberRepository;
//    @Mock
//    private ProductManagementRepository productManagementRepository;
//
//    @InjectMocks
//    private CartService cartService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("장바구니 추가")
//    void testAddCart() {
//        // given
//        CartRequestDto requestDto = new CartRequestDto(1L, 2L);
//        Long productMgtId = 1L;
//
//        Member member = mock(Member.class);
//        when(member.getId()).thenReturn(1L);
//
//        Product product = new Product();
//        product.setPrice(100); // 가격 설정
//
//        ProductManagement productMgt = new ProductManagement();
//        productMgt.setInventoryId(productMgtId);
//        productMgt.setProduct(product);
//
//        when(memberRepository.findById(requestDto.getMemberId())).thenReturn(Optional.of(member));
//        when(productManagementRepository.findById(productMgtId)).thenReturn(Optional.of(productMgt));
//        when(cartRepository.findByProductManagement(productMgt)).thenReturn(Optional.empty());
//
//        // when
//        cartService.addCart(requestDto, productMgtId);
//
//        // then
//        verify(cartRepository, times(1)).save(any(Cart.class));
//    }
//
//    @Test
//    @DisplayName("이미 있는 장바구니 옵션 추가할 경우")
//    void testAddCartWithExistingCart() {
//
//        // given
//        CartRequestDto requestDto = new CartRequestDto(1L, 2L);
//
//        Long productMgtId = 1L;
//        Member member = mock(Member.class);
//        when(member.getId()).thenReturn(1L);
//
//        ProductManagement productMgt = new ProductManagement();
//        productMgt.setInventoryId(productMgtId);
//
//        Product product = new Product();
//        product.setPrice(100); // 가격 설정
//        productMgt.setProduct(product);
//
//        Cart existingCart = new Cart();
//        existingCart.setCartId(1L);
//        existingCart.setQuantity(1L);
//        existingCart.setPrice(100L);
//        existingCart.setMember(member);
//
//        when(memberRepository.findById(requestDto.getMemberId())).thenReturn(Optional.of(member));
//        when(productManagementRepository.findById(productMgtId)).thenReturn(Optional.of(productMgt));
//        when(cartRepository.findByProductManagement(productMgt)).thenReturn(Optional.of(existingCart));
//
//        // when
//        Long cartId = cartService.addCart(requestDto, productMgtId);
//
//        // then
//        assertEquals(1L,cartId);
//        assertEquals(3L, existingCart.getQuantity());
//        assertEquals(300L, existingCart.getPrice());
//        verify(cartRepository, times(1)).save(existingCart);
//    }
//
//    @Test
//    @DisplayName("회원id로 장바구니 리스트 보기")
//    void testAllCart() {
//        // given
//        Long memberId = 1L;
//
//        Member member = mock(Member.class);
//        when(member.getId()).thenReturn(1L);
//
//        Product product1 = new Product();
//
//        ProductColor color = new ProductColor();
//
//        ProductManagement productMgt1 = new ProductManagement();
//        productMgt1.setProduct(product1);
//        productMgt1.setColor(color);
//        productMgt1.setSize(Size.FREE);
//
//        ProductManagement productMgt2 = new ProductManagement();
//        productMgt2.setProduct(product1);
//        productMgt2.setColor(color);
//        productMgt2.setSize(Size.MEDIUM);
//
//        Cart cart1 = new Cart();
//        cart1.setCartId(1L);
//        cart1.setMember(member);
//        cart1.setProductManagement(productMgt1);
//
//        Cart cart2 = new Cart();
//        cart2.setCartId(2L);
//        cart2.setMember(member);
//        cart2.setProductManagement(productMgt2);
//
//        List<Cart> carts = Arrays.asList(cart1, cart2);
//        when(cartRepository.findByMemberId(member.getId())).thenReturn(carts);
//
//        // when
//        List<CartDto> cartDtoList = cartService.allCarts(memberId);
//
//        // then
//        assertEquals(2, cartDtoList.size());
//    }
//
//    @Test
//    @DisplayName("장바구니 삭제")
//    void testDeleteCart() {
//        // given
//        Long cartId = 1L;
//        Cart cart = new Cart();
//        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
//
//        // when
//        cartService.deleteCart(cartId);
//
//        // then
//        verify(cartRepository, times(1)).delete(cart);
//    }
//
//
//}