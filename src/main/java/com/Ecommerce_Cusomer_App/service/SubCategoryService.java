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

import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
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

	public ResponseEntity<CommonApiResponse> addSubCategory(SubCategory category, MultipartFile image,
			Long categoryid) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (category == null || categoryid == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			SubCategory data = null;
			Boolean flag = true;
			Optional<Category> cate = categoryRepository.findById(categoryid);

			if (cate.isEmpty()) {
				response.setResponseMessage("No category Present,Failed to Add SubCategory Details ");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
				response.setResponseMessage("Sub Category Added successfully :-------->" + data);
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setResponseMessage("Category " + category.getName() + " Is Already Exists...");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to Update SubCategory

	public ResponseEntity<CommonApiResponse> updatesubCategoryDetails(SubCategory category, Long categoryid) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (category == null || categoryid == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Optional<SubCategory> cate = subCategoryRepository.findById(category.getId());

			if (cate.isEmpty()) {
				response.setResponseMessage("SubCategory Not Found..");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

			}
			SubCategory categ = cate.get();
			if (categoryid != categ.getCategory().getId()) {
				response.setResponseMessage("This SubCategory Not Belongs To ur Category, U Can't Update it ");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
				response.setResponseMessage("Failed to update category");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			response.setResponseMessage("Category Updated Successful");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to Update SubCategory Image

	public ResponseEntity<CommonApiResponse> updateSubCategoryImage(Long id, MultipartFile image, Long categoryid) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0 || categoryid == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			if (image == null) {
				response.setResponseMessage("Image is Not Selected");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			SubCategory category = subCategoryRepository.findById(id).get();
			String existingImage = category.getImage();
			if (categoryid != category.getCategory().getId()) {
				response.setResponseMessage("This SubCategory Not Belongs To ur Category, U Can't Update it ");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
				response.setResponseMessage("Failed to update Image");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			try {
				storageService.delete(existingImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setResponseMessage("Category Image Updated Successful");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to Fetch All SubCategories

	public ResponseEntity<SubCategoryResponseDto> fetchAllsubCategory() {
		SubCategoryResponseDto response = new SubCategoryResponseDto();
		try {
			List<SubCategory> categories = getAllCategoriesByStatusIn(Arrays.asList(CategoryStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(categories)) {

				response.setResponseMessage("No SubCategories found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			response.setCategories(categories);
			response.setResponseMessage("Category Fetched Successfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

	public ResponseEntity<SubCategoryResponseDto> fetchAllsubCategoryByCategoryId(Long categoryid) {

		SubCategoryResponseDto response = new SubCategoryResponseDto();
		try {
			if (categoryid == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Optional<Category> cat = categoryRepository.findById(categoryid);
			if (cat.isEmpty()) {
				response.setResponseMessage("No Category Present in this id:--> " + categoryid);
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Category catid = cat.get();

			List<SubCategory> subcat = getAllSubCategoriesByCategoryAndStatusIn(catid,
					Arrays.asList(SubCategoryStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(subcat)) {
				response.setResponseMessage("No SubCategories found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			response.setCategories(subcat);
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public List<SubCategory> getAllSubCategoriesByCategoryAndStatusIn(Category category, List<String> status) {
		return subCategoryRepository.findByCategoryAndStatusIn(category, status);
	}

	// Method To Fetch SubCategory Details by id

	public ResponseEntity<SubCategoryResponseDto> findById(Long id) {
		SubCategoryResponseDto response = new SubCategoryResponseDto();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Optional<SubCategory> category = subCategoryRepository.findById(id);
			if (category.isEmpty()) {
				response.setResponseMessage("No SubCategories found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			response.setResponseMessage("SubCategories Fetched Sucessfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to get All Categories By StatusIn

	private List<SubCategory> getAllCategoriesByStatusIn(List<String> status) {
		return subCategoryRepository.findByStatusIn(status);
	}

	// Method to delete Subcategory by Category Id

	public ResponseEntity<CommonApiResponse> deleteSubCategory(Long subcategoryId, Long categoryid) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (subcategoryId == 0 || categoryid == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			SubCategory category = subCategoryRepository.findById(subcategoryId).get();

			if (category == null) {
				response.setResponseMessage("SubCategory not found, failed to delete the SubCategory");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			if (categoryid != category.getCategory().getId()) {
				response.setResponseMessage("This SubCategory Not Belongs To ur Category, U Can't Delete it ");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			else {
				category.setCategory(null);
				subCategoryRepository.save(category);
				subCategoryRepository.delete(category);
				storageService.delete(category.getImage());
				response.setResponseMessage("Category Deleted Successful");
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to delete All SubCategories

	public ResponseEntity<CommonApiResponse> deleteAllSubCategory() {
		CommonApiResponse response = new CommonApiResponse();
		try {
			List<SubCategory> lst = subCategoryRepository.findAll();
			if (!lst.isEmpty()) {
				subCategoryRepository.deleteAll();
				storageService.deleteAll();
				response.setResponseMessage("All SubCategories Deleted Successful");
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setResponseMessage("No SubCategories are Present To Delete");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to fetch SubCategory by SubCategory Name

	public ResponseEntity<SubCategoryResponseDto> findByName(String name) {
		SubCategoryResponseDto response = new SubCategoryResponseDto();
		try {
			List<SubCategory> categories = findByNameContainingIgnoreCaseAndStatusIn(name,
					Arrays.asList(SubCategoryStatus.ACTIVE.value()));

			if (CollectionUtils.isEmpty(categories)) {

				response.setResponseMessage("No Category found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			response.setCategories(categories);
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private List<SubCategory> findByNameContainingIgnoreCaseAndStatusIn(String name, List<String> status) {

		return subCategoryRepository.findByNameContainingIgnoreCaseAndStatusIn(name, status);
	}

	public ResponseEntity<CommonApiResponse> statusUpdate(Long id) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Optional<SubCategory> sub = subCategoryRepository.findById(id);

			if (sub.isEmpty()) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			SubCategory ad = sub.get();
			if (ad.getStatus().contains(UserStatus.ACTIVE.value())) {
				ad.setStatus(UserStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(UserStatus.ACTIVE.value());
			}
			subCategoryRepository.save(ad);
			response.setResponseMessage("Status " + ad.getStatus() + " sucessfully");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> updateQuantity(Long id, int quantity) {

		CommonApiResponse response = new CommonApiResponse();

		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			SubCategory sub = subCategoryRepository.findByIdAndStatus(id, UserStatus.ACTIVE.value());

			if (sub == null) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			sub.setQuantity(quantity);

			SubCategory subCategory = subCategoryRepository.save(sub);
			if (subCategory.getQuantity() != quantity) {
				response.setResponseMessage("Failed to Update the quantity");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			response.setResponseMessage("Quantity Updated sucessfully,NO of Quantity = " + subCategory.getQuantity());
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public SubCategory getSubCategoryById(Long long1) {

		return subCategoryRepository.getSubCategoryById(long1);
	}

}
