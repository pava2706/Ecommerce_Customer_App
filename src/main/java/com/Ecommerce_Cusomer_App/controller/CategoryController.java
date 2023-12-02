package com.Ecommerce_Cusomer_App.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Ecommerce_Cusomer_App.dto.CategoryResponseDto;
import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.entity.Category;
import com.Ecommerce_Cusomer_App.service.CategoryService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	// Method to Add Category

	@PostMapping("/add")
	public ResponseEntity<CommonApiResponse> addCategory(@ModelAttribute Category category,
			@RequestParam("catimage") MultipartFile image) {
		return categoryService.addCategory(category, image);
	}

	// Method to Update Category

	@PutMapping("/update/details")
	public ResponseEntity<CommonApiResponse> updateCategoryDetails(@ModelAttribute Category category) {
		return categoryService.updateCategoryDetails(category);
	}

	// Method to Update CategoryImage

	@PutMapping("update/image")
	public ResponseEntity<CommonApiResponse> updateCategoryImage(Long id,
			@RequestParam("catimage") MultipartFile image) {
		return categoryService.updateCategoryImage(id, image);
	}

	// Method to fetch all category

	@GetMapping("/fetch/all")
	public ResponseEntity<CategoryResponseDto> fetchAllCategory() {
		return categoryService.fetchAllCategory();
	}

	// Method to delete category

	@DeleteMapping("/delete/byid/{categoryid}")
	public ResponseEntity<CommonApiResponse> deleteCategory(@PathVariable("categoryid") Long categoryId) {
		return categoryService.deleteCategory(categoryId);
	}

	// Method to delete All category

	@DeleteMapping("/delete/all")
	public ResponseEntity<CommonApiResponse> deleteAllCategory() {
		return categoryService.deleteAllCategory();
	}

	// Method to fetch image using image name

	@GetMapping("/fetch/image/{categoryImageName}")
	public ResponseEntity<byte[]> fetchFoodImage(@PathVariable("categoryImageName") String categoryImageName,
			HttpServletResponse resp) {

		return categoryService.fetchCategoryImage(categoryImageName);

	}

	// Method to fetch Category by using id

	@GetMapping("fetch/byid/{id}")
	public ResponseEntity<CategoryResponseDto> findById(@PathVariable("id") Long id) {
		return categoryService.findById(id);
	}

	// Method to fetch Category by Category Name

	@GetMapping("fetch/byname/{name}")
	public ResponseEntity<CategoryResponseDto> findByName(@PathVariable("name") String name) {
		return categoryService.findByName(name);
	}

	@PostMapping("/statusupdate/{id}")
	public ResponseEntity<CommonApiResponse> statusUpdate(@PathVariable("id") Long id) {
		return categoryService.statusUpdate(id);
	}

}
