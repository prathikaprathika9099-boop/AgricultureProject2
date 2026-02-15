package com.example.AgriProject.controller;

import com.example.AgriProject.dto.ProductUpdateRequestDto;
import com.example.AgriProject.dto.ProductUploadDto;
import com.example.AgriProject.dto.ProductUploadResponseDto;
import com.example.AgriProject.entity.Product;
import com.example.AgriProject.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "/upload/image",consumes = "multipart/form-data")
    public ResponseEntity<ProductUploadResponseDto> uploadProduct(@ModelAttribute ProductUploadDto productUploadDto)throws Exception{
        return productService.uploadProduct(productUploadDto);
    }

    @GetMapping("/get/product")
    public List<Product> getProducts(@RequestParam Long userId){
        return productService.getProducts(userId);
    }

    @GetMapping("/get/all/products")
    public  List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @PutMapping("/product/update/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequestDto dto){
        return productService.editProduct(id,dto);
    }

    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);

        return ResponseEntity.ok("Product deleted successfully");
    }
}
