package com.Ecommerce_Cusomer_App.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.Ecommerce_Cusomer_App.dto.SubCategoryResponseDto;
import com.Ecommerce_Cusomer_App.entity.Category;
import com.Ecommerce_Cusomer_App.entity.SubCategory;
import com.Ecommerce_Cusomer_App.repository.CategoryRepository;
import com.Ecommerce_Cusomer_App.repository.SubCategoryRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.CategoryStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.SubCategoryStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.UserStatus;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;

@Lazy
@Service
public class SubCategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SubCategoryRepository subCategoryRepository;

	@Autowired
	private StorageService storageService;

	// Method to Add SubCategory

	public ResponseEntity<Object> addSubCategory(SubCategory category, MultipartFile image, Long categoryid) {
		try {
			if (category == null || categoryid == 0) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}
			SubCategory data = null;
			Boolean flag = true;
			Optional<Category> cate = categoryRepository.findById(categoryid);

			if (cate.isEmpty()) {
				return new ResponseEntity<Object>("No category Present,Failed to Add SubCategory Details ",
						HttpStatus.BAD_REQUEST);
			}
			Category cat = cate.get();
			SubCategory subCategory = new SubCategory();
			List<SubCategory> lst = subCategoryRepository.findAll();// List of Categories
			for (SubCategory name : lst) {
				if (name.getName().equals(category.getName())) {
					flag = false;
					break;
				}
				flag = true;
			}
			if (flag) {
				subCategory.setName(category.getName());
				subCategory.setDescription(category.getDescription());
				subCategory.setPrice(category.getPrice());
				subCategory.setStatus(SubCategoryStatus.ACTIVE.value());
				String img = storageService.store(image);
				subCategory.setImage(img);
				subCategory.setCategory(cat);
				subCategory.setQuantity(category.getQuantity());
				data = subCategoryRepository.save(subCategory);
				return CustomerUtils.getResponseEntity("Sub Category Added successfully :-------->" + data,
						HttpStatus.OK);
			}
			return new ResponseEntity<Object>("Category " + category.getName() + " Is Already Exists...",
					HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to Update SubCategory

	public ResponseEntity<Object> updatesubCategoryDetails(SubCategory category, Long categoryid) {
		try {
			if (category == null || categoryid == 0) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}

			Optional<SubCategory> cate = subCategoryRepository.findById(category.getId());

			if (cate.isEmpty()) {
				return CustomerUtils.getResponseEntity("SubCategory Not Found..", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			SubCategory categ = cate.get();
			if (categoryid != categ.getCategory().getId()) {
				return CustomerUtils.getResponseEntity(
						"This SubCategory Not Belongs To ur Category, U Can't Update it ",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			categ.setName(category.getName());
			categ.setDescription(category.getDescription());
			categ.setPrice(category.getPrice());
			categ.setStatus(SubCategoryStatus.ACTIVE.value());
			categ.setImage(categ.getImage());
			categ.setCategory(categ.getCategory());
			categ.setQuantity(category.getQuantity());
			SubCategory savedCategory = subCategoryRepository.save(categ);

			if (savedCategory == null) {
				return new ResponseEntity<Object>("Failed to update category", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return CustomerUtils.getResponseEntity("Category Updated Successful", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Method to Update SubCategory Image

	public ResponseEntity<Object> updateSubCategoryImage(Long id, MultipartFile image, Long categoryid) {
		try {
			if (id == 0 || categoryid == 0) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}
			if (image == null) {
				return new ResponseEntity<>("Image is Not Selected", HttpStatus.BAD_REQUEST);
			}
			SubCategory category = subCategoryRepository.findById(id).get();
			String existingImage = category.getImage();
			if (categoryid != category.getCategory().getId()) {
				return CustomerUtils.getResponseEntity(
						"This SubCategory Not Belongs To ur Category, U Can't Update it ",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			// store updated food image in Image Folder and give name to store in
			// database
			category.setName(category.getName());
			category.setDescription(category.getDescription());
			category.setStatus(SubCategoryStatus.ACTIVE.value());
			category.setImage(storageService.store(image));
			category.setPrice(category.getPrice());
			category.setCategory(category.getCategory());
			SubCategory updatedCategory = subCategoryRepository.save(category);

			if (updatedCategory == null) {
				return new ResponseEntity<Object>("Failed to update Image", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				storageService.delete(existingImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return CustomerUtils.getResponseEntity("Category Image Updated Successful", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Method to Fetch All SubCategories

	public ResponseEntity<Object> fetchAllsubCategory() {
		try {
			SubCategoryResponseDto response = new SubCategoryResponseDto();
			List<SubCategory> categories = getAllCategoriesByStatusIn(Arrays.asList(CategoryStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(categories)) {

				return new ResponseEntity<Object>("No SubCategories found", HttpStatus.NOT_FOUND);
			}

			response.setCategories(categories);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Method to fetch subcategoryImage by subcategoryImageName and Category Id

//	public ResponseEntity<byte[]> fetchSubCategoryImage(String categoryImageName, Long categoryid) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	// Method to fetch subcategoryImage by subcategoryImageName

	public ResponseEntity<byte[]> fetchSubCategoryImage(String categoryImageName) {
		try {
			Resource resource = storageService.load(categoryImageName);

			if (resource != null && resource.exists()) {
				try (InputStream in = resource.getInputStream()) {
					byte[] imageBytes = IOUtils.toByteArray(in);

					// Set appropriate content type based on the image type
					// resp.setContentType("image/jpeg"); // Replace with the correct image type
					ResponseEntity<byte[]> imageResponseEntity = CustomerUtils.getImageResponseEntity(imageBytes,
							MediaType.IMAGE_JPEG);
					return imageResponseEntity;
				} catch (IOException e) {
					e.printStackTrace();
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Method to fetch all Subcategories by Category Id

	public ResponseEntity<Object> fetchAllsubCategoryByCategoryId(Long categoryid) {

		try {
			if (categoryid == 0) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}

			Optional<Category> cat = categoryRepository.findById(categoryid);
			if (cat.isEmpty()) {
				return new ResponseEntity<>("No Category Present in this id:--> " + categoryid, HttpStatus.BAD_REQUEST);
			}

			Category catid = cat.get();

			SubCategoryResponseDto response = new SubCategoryResponseDto();

			List<SubCategory> subcat = getAllSubCategoriesByCategoryAndStatusIn(catid,
					Arrays.asList(SubCategoryStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(subcat)) {
				return new ResponseEntity<>("No SubCategories found", HttpStatus.OK);
			}

			response.setCategories(subcat);

			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public List<SubCategory> getAllSubCategoriesByCategoryAndStatusIn(Category category, List<String> status) {
		return subCategoryRepository.findByCategoryAndStatusIn(category, status);
	}

	// Method To Fetch SubCategory Details by id

	public ResponseEntity<Object> findById(Long id) {
		try {
			if (id == 0) {
				return new ResponseEntity<>("missing input", HttpStatus.BAD_REQUEST);
			}
			Optional<SubCategory> category = subCategoryRepository.findById(id);
			if (category.isEmpty()) {
				return new ResponseEntity<Object>("No SubCategories found", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Object>(category.get(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Method to get All Categories By StatusIn

	private List<SubCategory> getAllCategoriesByStatusIn(List<String> status) {
		return subCategoryRepository.findByStatusIn(status);
	}

	// Method to delete Subcategory by Category Id

	public ResponseEntity<Object> deleteSubCategory(Long subcategoryId, Long categoryid) {

		try {
			if (subcategoryId == 0 || categoryid == 0) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}

			SubCategory category = subCategoryRepository.findById(subcategoryId).get();

			if (category == null) {
				return new ResponseEntity<Object>("SubCategory not found, failed to delete the SubCategory",
						HttpStatus.NOT_FOUND);
			}

			if (categoryid != category.getCategory().getId()) {
				return CustomerUtils.getResponseEntity(
						"This SubCategory Not Belongs To ur Category, U Can't Delete it ",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			else {
				category.setCategory(null);
				subCategoryRepository.save(category);
				subCategoryRepository.delete(category);
				storageService.delete(category.getImage());
				return CustomerUtils.getResponseEntity("Category Deleted Successful", HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Method to delete All SubCategories

	public ResponseEntity<Object> deleteAllSubCategory() {
		try {
			List<SubCategory> lst = subCategoryRepository.findAll();
			if (!lst.isEmpty()) {
				subCategoryRepository.deleteAll();
				storageService.deleteAll();
				return CustomerUtils.getResponseEntity("All SubCategories Deleted Successful", HttpStatus.OK);
			} else {
				return CustomerUtils.getResponseEntity("No SubCategories are Present To Delete",
						HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to fetch SubCategory by SubCategory Name

	public ResponseEntity<Object> findByName(String name) {
		try {
			List<SubCategory> categories = findByNameContainingIgnoreCaseAndStatusIn(name,
					Arrays.asList(SubCategoryStatus.ACTIVE.value()));

			SubCategoryResponseDto response = new SubCategoryResponseDto();

			if (CollectionUtils.isEmpty(categories)) {

				return new ResponseEntity<Object>("No Category found", HttpStatus.NOT_FOUND);
			}

			response.setCategories(categories);
			System.out.println(response);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private List<SubCategory> findByNameContainingIgnoreCaseAndStatusIn(String name, List<String> status) {

		return subCategoryRepository.findByNameContainingIgnoreCaseAndStatusIn(name, status);
	}

	public ResponseEntity<Object> statusUpdate(Long id) {
		try {
			if (id == 0) {
				return new ResponseEntity<>("Missing Input", HttpStatus.BAD_REQUEST);
			}
			Optional<SubCategory> sub = subCategoryRepository.findById(id);

			if (sub.isEmpty()) {
				return new ResponseEntity<Object>("No User Found", HttpStatus.NOT_FOUND);
			}

			SubCategory ad = sub.get();
			if (ad.getStatus().contains(UserStatus.ACTIVE.value())) {
				ad.setStatus(UserStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(UserStatus.ACTIVE.value());
			}
			subCategoryRepository.save(ad);
			return new ResponseEntity<>("Status " + ad.getStatus() + " sucessfully", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public ResponseEntity<Object> updateQuantity(Long id, int quantity) {

		try {
			if (id == 0) {
				return new ResponseEntity<>("Missing Input", HttpStatus.BAD_REQUEST);
			}
			SubCategory sub = subCategoryRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (sub == null) {
				return new ResponseEntity<Object>("No User Found", HttpStatus.NOT_FOUND);
			}

			sub.setQuantity(quantity);

			SubCategory subCategory = subCategoryRepository.save(sub);
			if (subCategory.getQuantity() != quantity) {
				return new ResponseEntity<>("Failed to Update the quantity", HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Quantity Updated sucessfully,NO of Quantity = " + subCategory.getQuantity(),
					HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public SubCategory getSubCategoryById(int id) {
		
		return subCategoryRepository.getSubCategoryById(id);
	}

}
