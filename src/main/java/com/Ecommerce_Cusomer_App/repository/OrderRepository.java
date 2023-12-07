package com.Ecommerce_Cusomer_App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce_Cusomer_App.entity.Orders;
import com.Ecommerce_Cusomer_App.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

	List<Orders> findByUser(User user);

	List<Orders> findByOrderId(String orderId);

}
