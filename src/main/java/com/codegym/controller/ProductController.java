package com.codegym.controller;

import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private IProductService productService;

    @Value("${file-upload}")
    private String uploadPath;

    @GetMapping
    public ResponseEntity<Page<Product>> findAllProduct(@RequestParam(name = "q")Optional<String> q, @PageableDefault(value = 5)Pageable pageable){
        Page<Product> products;
        if(!q.isPresent()){
            products = productService.findAll(pageable);
        }else {
            products = productService.findByName(q.get(),pageable);
        }
        if(products.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable Long id){
        Optional<Product> productOptional = productService.findById(id);
        if(!productOptional.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(productOptional.get(),HttpStatus.OK);
    }

//    @PostMapping
//    public ResponseEntity<Product> createProduct(@RequestBody Product product){
//        return new ResponseEntity<>(productService.save(product),HttpStatus.CREATED);
//    }

//    upload-file
    @PostMapping
    public ResponseEntity<Product> save(@ModelAttribute ProductForm productForm){
        MultipartFile image = productForm.getImage();
        if(image.getSize() !=0){
            String fileName = productForm.getImage().getOriginalFilename();
            try {
                FileCopyUtils.copy(productForm.getImage().getBytes(),new File(uploadPath + fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Product product = new Product(productForm.getId(),productForm.getName(),productForm.getPrice(),productForm.getQuantity(),productForm.getDescription(),fileName);
            product.setCategory(productForm.getCategory());
            return new ResponseEntity<>(productService.save(product),HttpStatus.CREATED);

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id,@RequestBody Product product){
        Optional<Product> productOptional = productService.findById(id);
        if(!productOptional.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        product.setId(productOptional.get().getId());
        return new ResponseEntity<>(productService.save(product),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id){
        Optional<Product> productOptional = productService.findById(id);
        if(!productOptional.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productService.remove(id);
        return new ResponseEntity<>(productOptional.get(),HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<Page<Product>> findByCategory(@PathVariable Long id,@PageableDefault(value = 5) Pageable pageable){
        Page<Product> products = productService.findByCategory(id,pageable);
        if(products.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products,HttpStatus.OK);
    }
}
