package com.syed.springtransactions5.controller;

import com.syed.springtransactions5.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public void saveProduct() {
        productService.addTenProduct();
    }
}
