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

import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.dto.SubCategoryResponseDto;
import com.Ecommerce_Cusomer_App.entity.SubCategory;
import com.Ecommerce_Cusomer_App.service.SubCategoryService;

@RestController
@RequestMapping("api/subcategory")
public class SubCategoryController {

	@Autowired
	private SubCategoryService subcategoryService;

	// Method to Add Category

	@PostMapping("/add/{categoryid}")
	public ResponseEntity<CommonApiResponse> addSubCategory(@ModelAttribute SubCategory category,
			@RequestParam("catimage") MultipartFile image, @PathVariable("categoryid") Long categoryid) {
		return subcategoryService.addSubCategory(category, image, categoryid);
	}

	// Method to Update Category

	@PutMapping("/update/details/{categoryid}")
	public ResponseEntity<CommonApiResponse> updateSubCategoryDetails(@ModelAttribute SubCategory category,
			@PathVariable("categoryid") Long categoryid) {
		System.out.println(category);
		return subcategoryService.updatesubCategoryDetails(category, categoryid);
	}

	// Method to Update CategoryImage by Category Id

	@PutMapping("update/image/{categoryid}")
	public ResponseEntity<CommonApiResponse> updateSubCategoryImage(Long id,
			@RequestParam("catimage") MultipartFile image, @PathVariable("categoryid") Long categoryid) {
		return subcategoryService.updateSubCategoryImage(id, image, categoryid);
	}

	// Method to fetch all Subcategories

	@GetMapping("/fetch/all")
	public ResponseEntity<SubCategoryResponseDto> fetchAllSubCategory() {
		return subcategoryService.fetchAllsubCategory();
	}

	// Method to delete Subcategory by Category Id

	@DeleteMapping("/delete/byid/{subcategoryid}/{categoryid}")
	public ResponseEntity<CommonApiResponse> deleteSubCategory(@PathVariable("subcategoryid") Long subcategoryId,
			@PathVariable("categoryid") Long categoryid) {
		return subcategoryService.deleteSubCategory(subcategoryId, categoryid);
	}

	// Method to delete All Subcategories

	@DeleteMapping("/delete/all")
	public ResponseEntity<CommonApiResponse> deleteAllSubCategory() {
		return subcategoryService.deleteAllSubCategory();
	}

	// Method to fetch subcategoryImage by subcategoryImageName

	@GetMapping("/fetch/image/{subcategoryImageName}")
	public ResponseEntity<byte[]> fetchSubCategoryImage(
			@PathVariable("subcategoryImageName") String subcategoryImageName) {

		return subcategoryService.fetchSubCategoryImage(subcategoryImageName);
	}

	// Method to fetch subcategoryImage by subcategoryImageName and Category Id

//	@GetMapping("/fetch/image/{categoryImageName}/{categoryid}")
//	public ResponseEntity<byte[]> fetchSubCategoryImageByCategoryid(
//			@PathVariable("categoryImageName") String categoryImageName, @Param("categoryid") Long categoryid) {
//
//		return subcategoryService.fetchSubCategoryImage(categoryImageName, categoryid);
//
//	}

	// Method to fetch all Subcategories by Category Id

	@GetMapping("/fetch/all/{categoryid}")
	public ResponseEntity<SubCategoryResponseDto> fetchAllSubCategoryByCategoryId(
			@PathVariable("categoryid") Long categoryid) {
		return subcategoryService.fetchAllsubCategoryByCategoryId(categoryid);
	}

	// Method To Fetch SubCategory Details by id

	@GetMapping("fetch/byid/{id}")
	public ResponseEntity<SubCategoryResponseDto> findById(@PathVariable("id") Long id) {
		return subcategoryService.findById(id);
	}

	// Method to fetch SubCategory by SubCategory Name

	@GetMapping("fetch/byname/{name}")
	public ResponseEntity<SubCategoryResponseDto> findByName(@PathVariable("name") String name) {
		return subcategoryService.findByName(name);
	}

	// Method to Update the Status by id

	@PostMapping("/statusupdate/{id}")
	public ResponseEntity<CommonApiResponse> statusUpdate(@PathVariable("id") Long id) {
		return subcategoryService.statusUpdate(id);
	}

	// Method to Update the Quantity by id

	@PostMapping("/statusupdate/{id}/{quantity}")
	public ResponseEntity<CommonApiResponse> updateQuantity(@PathVariable("id") Long id,
			@PathVariable("quantity") int quantity) {
		return subcategoryService.updateQuantity(id, quantity);
	}

}
