package com.Ecommerce_Cusomer_App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce_Cusomer_App.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findByStatusIn(List<String> status);

	Category findByNameAndStatus(String name, String status);

	List<Category> findByNameContainingIgnoreCaseAndStatusIn(String name, List<String> status);
}
