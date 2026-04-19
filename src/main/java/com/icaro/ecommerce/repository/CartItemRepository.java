package com.icaro.ecommerce.repository;

import com.icaro.ecommerce.model.Cart;
import com.icaro.ecommerce.model.CartItem;
import com.icaro.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
