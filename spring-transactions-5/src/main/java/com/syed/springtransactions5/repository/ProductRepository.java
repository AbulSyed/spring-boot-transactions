package com.syed.springtransactions5.repository;

import com.syed.springtransactions5.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

//    @Transactional(propagation = Propagation.SUPPORTS)
    @Modifying
    @Query(value = """
            INSERT INTO products (name)
            VALUES (:name)
        """, nativeQuery = true)
    void saveProduct(String name);
}
