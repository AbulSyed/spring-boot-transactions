package com.syed.springtransactions5.service;

import com.syed.springtransactions5.entity.ProductEntity;
import com.syed.springtransactions5.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

//    @Transactional
//    public void addTenProduct() {
//        for (int i = 1; i <= 10; i++) {
//            productRepository.saveProduct("product " + i);
//
////            if (i == 5) {
////                throw new RuntimeException("something went wrong");
////            }
//        }
//    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void addTenProduct() {
        for (int i = 1; i <= 10; i++) {
            productRepository.saveProduct("product " + i);
        }
    }
}
