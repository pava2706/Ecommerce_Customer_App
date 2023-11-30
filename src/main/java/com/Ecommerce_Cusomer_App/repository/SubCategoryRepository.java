package com.Ecommerce_Cusomer_App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce_Cusomer_App.entity.Category;
import com.Ecommerce_Cusomer_App.entity.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

	List<SubCategory> findByStatusIn(List<String> status);

	List<SubCategory> findByCategoryAndStatusIn(Category category, List<String> status);

	SubCategory findByName(String name);

	List<SubCategory> findByNameContainingIgnoreCaseAndStatusIn(String name, List<String> status);

	SubCategory findByIdAndStatus(Long id, String value);

	SubCategory getSubCategoryById(int id);
}
