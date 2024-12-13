package com.mysite.crud2.services;

import com.mysite.crud2.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Integer id(int id);
}
