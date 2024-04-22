//package PU.pushop.cart.service;
//
//import PU.pushop.cart.entity.Cart;
//import PU.pushop.cart.model.CartDto;
//import PU.pushop.cart.model.CartRequestDto;
//import PU.pushop.cart.repository.CartRepository;
//import PU.pushop.category.entity.Category;
//import PU.pushop.category.repository.CategoryRepository;
//import PU.pushop.members.entity.Member;
//import PU.pushop.members.entity.enums.MemberRole;
//import PU.pushop.members.entity.enums.SocialType;
//import PU.pushop.members.repository.MemberRepositoryV1;
//import PU.pushop.product.entity.Product;
//import PU.pushop.product.entity.ProductColor;
//import PU.pushop.product.repository.ProductColorRepository;
//import PU.pushop.product.repository.ProductRepositoryV1;
//import PU.pushop.productManagement.entity.ProductManagement;
//import PU.pushop.productManagement.entity.enums.Size;
//import PU.pushop.productManagement.repository.ProductManagementRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//@TestMethodOrder(MethodOrderer.MethodName.class)
//public class CartServiceIntegrationTest {
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private CartRepository cartRepository;
//    @Autowired
//    private ProductManagementRepository productManagementRepository;
//    @Autowired
//    private MemberRepositoryV1 memberRepository;
//    @Autowired
//    private ProductRepositoryV1 productRepository;
//    @Autowired
//    private ProductColorRepository colorRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    private Member createMember() {
//        Member member = new Member("test@test.com","123435dsfsd", "username","nickname", MemberRole.USER, SocialType.GENERAL,null,"token",true);
//        return memberRepository.save(member);
//    }
//
//    private Product createProduct() {
//        Product product = new Product();
//        product.setPrice(100);
//        product.setProductName("test product");
//        product.setProductInfo("test product info");
//        product.setManufacturer("manufacurer");
//        return productRepository.save(product);
//    }
//
//    private ProductColor createProductColor1() {
//        ProductColor color1 = new ProductColor();
//        color1.setColor("white");
//        return colorRepository.save(color1);
//    }
//    private ProductColor createProductColor2() {
//        ProductColor color2 = new ProductColor();
//        color2.setColor("black");
//        return colorRepository.save(color2);
//    }
//
//    private Category createCategory() {
//        Category category = new Category();
//        category.setName("치마");
//        category.setDepth(1L);
//        return categoryRepository.save(category);
//    }
//
//    private ProductManagement createProductManagement(Product product, ProductColor color, Category category) {
//        ProductManagement productManagement = new ProductManagement();
//        productManagement.setProduct(product);
//        productManagement.setColor(color);
//        productManagement.setCategory(category);
//        productManagement.setSize(Size.FREE);
//        return productManagementRepository.save(productManagement);
//    }
//
//
//    @Test
//    @DisplayName("장바구니 등록")
//    void testAddCart() {
//        // given
//        Member member = createMember();
//        Product product = createProduct();
//        ProductColor color = createProductColor1();
//        Category category = createCategory();
//        ProductManagement productManagement = createProductManagement(product, color, category);
//
//
//        CartRequestDto requestDto = new CartRequestDto(member.getId(), 2L);
//
//        // when
//        Long cartId = cartService.addCart(requestDto, productManagement.getInventoryId());
//
//        // then
//        assertNotNull(cartId);
//
//        Cart savedCart = cartRepository.findById(cartId).orElse(null);
//        assertNotNull(savedCart);
//        assertEquals(2L, savedCart.getQuantity());
//        assertEquals(200L, savedCart.getPrice());
//        assertEquals(member.getId(), savedCart.getMember().getId());
//    }
//
//    @Test
//    @DisplayName("이미 있는 상품 장바구니 추가")
//    void testAddCartWhenExistingCart() {
//
//        // given
//        Member member = createMember();
//        Product product = createProduct();
//        ProductColor color = createProductColor1();
//        Category category = createCategory();
//        ProductManagement productManagement = createProductManagement(product, color, category);
//
//        Cart existingCart = new Cart();
//        existingCart.setMember(member);
//        existingCart.setProductManagement(productManagement);
//        existingCart.setQuantity(1L);
//        existingCart.setPrice(100L);
//        cartRepository.save(existingCart);
//
//        CartRequestDto requestDto = new CartRequestDto(member.getId(), 2L);
//
//        // when
//        Long cartId = cartService.addCart(requestDto, productManagement.getInventoryId());
//
//        // then
//        assertNotNull(cartId);
//
//        Cart savedCart = cartRepository.findById(cartId).orElse(null);
//        assertNotNull(savedCart);
//        assertEquals(3L, savedCart.getQuantity()); // 기존 1 + 새로 추가된 2
//        assertEquals(300L, savedCart.getPrice()); // 기존 가격 100 + 새로 추가된 200
//    }
//
//    @Test
//    @DisplayName("내 장바구니 보기")
//    void testAllCarts() {
//        // given
//        Member member = createMember();
//        Product product = createProduct();
//        ProductColor color1 = createProductColor1();
//        ProductColor color2 = createProductColor2();
//        Category category = createCategory();
//        ProductManagement productManagement1 = createProductManagement(product, color1, category);
//        ProductManagement productManagement2 = createProductManagement(product, color2, category);
//
//        Cart cart1 = new Cart();
//        cart1.setMember(memberRepository.findById(member.getId()).orElse(null));
//        cart1.setProductManagement(productManagement1);
//        cart1.setQuantity(1L);
//        cart1.setPrice(100L);
//        cartRepository.save(cart1);
//
//        Cart cart2 = new Cart();
//        cart2.setMember(memberRepository.findById(member.getId()).orElse(null));
//        cart2.setProductManagement(productManagement2);
//        cart2.setQuantity(3L);
//        cart2.setPrice(300L);
//        cartRepository.save(cart2);
//
//        // when
//        List<CartDto> cartDtoList = cartService.allCarts(member.getId());
//
//        // then
//        assertNotNull(cartDtoList);
//        assertEquals(2, cartDtoList.size());
//    }
//
//    @Test
//    @DisplayName("장바구니 수정")
//    void testUpdateCart() {
//
//        // given
//        Member member = createMember();
//        Product product = createProduct();
//        ProductColor color = createProductColor1();
//        Category category = createCategory();
//        ProductManagement productManagement = createProductManagement(product, color, category);
//
//        Cart cart = new Cart();
//        cart.setMember(member);
//        cart.setProductManagement(productManagement);
//        cart.setQuantity(1L);
//        cart.setPrice(100L);
//        cartRepository.save(cart);
//
//        Long cartId = cart.getCartId();
//
//
//        Cart updatedCart = new Cart();
//        updatedCart.setQuantity(3L);
//
//        // when
//        Cart result = cartService.updateCart(cartId, updatedCart);
//
//        // then
//        assertNotNull(result);
//        assertEquals(3L, result.getQuantity());
//        assertEquals(300L, result.getPrice());
//    }
//
//    @Test
//    @DisplayName("장바구니 삭제")
//    void testDeleteCart() {
//        // given
//        Cart cart = new Cart();
//        cart.setQuantity(2L);
//        cart.setPrice(200L);
//        cartRepository.save(cart);
//
//        Long cartId = cart.getCartId();
//
//        // when
//        cartService.deleteCart(cartId);
//
//        // then
//        assertFalse(cartRepository.findById(cartId).isPresent());
//    }
//}
