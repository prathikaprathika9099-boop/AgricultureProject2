package com.example.AgriProject.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.AgriProject.dto.ProductUpdateRequestDto;
import com.example.AgriProject.dto.ProductUploadDto;
import com.example.AgriProject.dto.ProductUploadResponseDto;
import com.example.AgriProject.entity.Cart;
import com.example.AgriProject.entity.CartItem;
import com.example.AgriProject.entity.Product;
import com.example.AgriProject.entity.User;
import com.example.AgriProject.repository.CartItemRepository;
import com.example.AgriProject.repository.CartRepository;
import com.example.AgriProject.repository.ProductRepository;
import com.example.AgriProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;

    private final ModelMapper modelMapper;

    @Autowired
    private Cloudinary cloudinary;

    public ResponseEntity<ProductUploadResponseDto> uploadProduct(ProductUploadDto productUploadDto) throws Exception{
        Map uploadResult = cloudinary.uploader().upload(
                productUploadDto.getImage().getBytes(),
                ObjectUtils.asMap("folder","agribazar")
        );

        String imageUrl = uploadResult.get("secure_url").toString();

        User user = userRepository.findById(productUploadDto.getUserId()).orElseThrow(()->
                new IllegalArgumentException("User not found"));

        Product product = Product.builder()
                .name(productUploadDto.getName())
                .imageUrl(imageUrl)
                .stock(productUploadDto.getStock())
                .cost(productUploadDto.getCost())
                .description(productUploadDto.getDescription())
                .user(user)
                .reservedStock(0)
                .build();

        Product saved = productRepository.save(product);

        ProductUploadResponseDto productUploadResponseDto=modelMapper.map(saved,ProductUploadResponseDto.class);

        return ResponseEntity.ok(productUploadResponseDto);
    }

    public List<Product> getProducts(Long userId) {
        return productRepository.findByUserId(userId);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public ResponseEntity<String> editProduct(Long id,ProductUpdateRequestDto dto){
        try{
            Product product=productRepository.findById(id).orElse(null);

            if(product==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            boolean isPriceUpdated = dto.getPrice()!=null;
            boolean isStockUpdated = dto.getStock()!=null;

            if (dto.getPrice() != null && dto.getPrice() < 0) {
                return ResponseEntity.badRequest().body("Price cannot be negative");
            }

            if (dto.getStock() != null && dto.getStock() < 0) {
                return ResponseEntity.badRequest().body("Stock cannot be negative");
            }

            if (isPriceUpdated) {
                product.setCost(dto.getPrice());
            }

            if (isStockUpdated) {
                product.setStock(dto.getStock());
            }
            productRepository.save(product);

            if(isStockUpdated){
                int newStock=product.getStock();

                if(newStock==0)
                    cartItemRepository.deleteCartItemsIfStockZero(id,newStock);

                else
                    cartItemRepository.reduceCartItemsQuantityIfStockLess(id,newStock);
            }

            if(isPriceUpdated){
                cartItemRepository.updateCartItemsPrice(id,product.getCost());
            }

            if(isPriceUpdated || isStockUpdated){
                cartRepository.recalculateTotalForCartsContainingProduct(id);
            }

            return ResponseEntity.ok("Product updated successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product");
        }
    }

    @Transactional
    public void deleteProduct(Long id) {

        // Load only carts that contain this product
        List<Cart> carts = cartRepository.findAll();

        for (Cart cart : carts) {

            cart.getItems().removeIf(
                    item -> item.getProduct().getId().equals(id)
            );

            double newTotal = cart.getItems().stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();

            cart.setTotalAmount(newTotal);
        }

        productRepository.deleteById(id);
    }
}
