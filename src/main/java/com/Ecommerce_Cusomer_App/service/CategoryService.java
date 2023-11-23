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

import com.Ecommerce_Cusomer_App.dto.CategoryResponseDto;
import com.Ecommerce_Cusomer_App.entity.Category;
import com.Ecommerce_Cusomer_App.repository.CategoryRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.CategoryStatus;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;

@Lazy
@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private StorageService storageService;

	// private final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

	public ResponseEntity<Object> addCategory(Category category, MultipartFile image) {
		try {
			if (category == null) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}
			Category data = null;
			Boolean flag = true;
			Category cat = new Category();
			List<Category> lst = categoryRepository.findAll();// List of Categories
			for (Category name : lst) {
				System.out.println(1);
				if (name.getName().equals(category.getName())) {
					System.out.println(2);
					flag = false;
					break;
				}
				flag = true;
			}
			if (flag) {
				cat.setName(category.getName());
				cat.setDescription(category.getDescription());
				cat.setStatus(CategoryStatus.ACTIVE.value());
				String img = storageService.store(image);
				cat.setImage(img);
				data = categoryRepository.save(cat);
				return CustomerUtils.getResponseEntity("Category Added successfully", HttpStatus.OK);
			}
			return new ResponseEntity<Object>("Category " + category.getName() + " Is Already Exists...",
					HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<Object> updateCategoryDetails(Category category) {
		try {
			if (category == null) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}

			Category categ = categoryRepository.findById(category.getId()).get();

			if (categ == null) {
				return CustomerUtils.getResponseEntity("Category Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			categ.setName(category.getName());
			categ.setDescription(category.getDescription());
			categ.setStatus(CategoryStatus.ACTIVE.value());
			categ.setImage(categ.getImage());
			Category savedCategory = categoryRepository.save(categ);

			if (savedCategory == null) {
				return new ResponseEntity<Object>("Failed to update category", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return CustomerUtils.getResponseEntity("Category Updated Successful", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<Object> updateCategoryImage(Long id, MultipartFile image) {
		try {
			if (id == 0) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}
			if (image == null) {
				return new ResponseEntity<Object>("Image is Not Selected", HttpStatus.BAD_REQUEST);
			}
			Category category = categoryRepository.findById(id).get();
			String existingImage = category.getImage();

			// store updated food image in Image Folder and give name to store in
			// database
			category.setName(category.getName());
			category.setDescription(category.getDescription());
			category.setStatus(CategoryStatus.ACTIVE.value());
			category.setImage(storageService.store(image));

			Category updatedCategory = categoryRepository.save(category);

			if (updatedCategory == null) {
				return new ResponseEntity<Object>("Failed to update category", HttpStatus.INTERNAL_SERVER_ERROR);
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

	public ResponseEntity<Object> fetchAllCategory() {
		try {
			CategoryResponseDto response = new CategoryResponseDto();
			List<Category> categories = getAllCAtegoriesByStatusIn(Arrays.asList(CategoryStatus.ACTIVE.value()));

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

	private List<Category> getAllCAtegoriesByStatusIn(List<String> status) {
		return categoryRepository.findByStatusIn(status);
	}

	// Method To Delete Category

	public ResponseEntity<Object> deleteCategory(Long categoryId) {
		try {
			if (categoryId == null) {
				return new ResponseEntity<Object>("missing input", HttpStatus.BAD_REQUEST);
			}

			Category category = categoryRepository.findById(categoryId).get();
			if (category == null) {
				return new ResponseEntity<Object>("Category not found, failed to delete the Category",
						HttpStatus.NOT_FOUND);
			} else {
				categoryRepository.delete(category);
				storageService.delete(category.getImage());
				return CustomerUtils.getResponseEntity("Category Deleted Successful", HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// Method to fetch CategoryImage by ImageName

	public ResponseEntity<byte[]> fetchCategoryImage(String CategoryImageName) {
		try {
//			Category cat = categoryRepository.findByName(foodImageName);
//			System.out.println(cat);
			Resource resource = storageService.load(CategoryImageName);
			System.out.println(resource);
			System.out.println(CategoryImageName);
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

	public ResponseEntity<Object> findById(Long id) {
		try {
			Optional<Category> cate = categoryRepository.findById(id);
			if (cate.isEmpty()) {
				return new ResponseEntity<Object>("No Category found", HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<Object>(cate, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	public ResponseEntity<Object> deleteAllCategory() {
		try {
			List<Category> lst = categoryRepository.findAll();
			if (!lst.isEmpty()) {
				categoryRepository.deleteAll();
				storageService.deleteAll();
				return CustomerUtils.getResponseEntity("All Categories Deleted Successful", HttpStatus.OK);
			} else {
				return CustomerUtils.getResponseEntity("No Categories are Present To Delete", HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CustomerUtils.getResponseEntity("SOMETHING_WENT_WRONG", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
