package com.Ecommerce_Cusomer_App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce_Cusomer_App.entity.Cart;
import com.Ecommerce_Cusomer_App.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	List<Cart> findByUser(User user);

	Cart getCartById(int id);

}
