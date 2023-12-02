package com.Ecommerce_Cusomer_App.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

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
import com.Ecommerce_Cusomer_App.dto.CommonApiResponse;
import com.Ecommerce_Cusomer_App.entity.Category;
import com.Ecommerce_Cusomer_App.entity.SubCategory;
import com.Ecommerce_Cusomer_App.repository.CategoryRepository;
import com.Ecommerce_Cusomer_App.utils.Constants.CategoryStatus;
import com.Ecommerce_Cusomer_App.utils.Constants.SubCategoryStatus;
import com.Ecommerce_Cusomer_App.utils.CustomerUtils;

@Lazy
@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private StorageService storageService;

	@Autowired
	private SubCategoryService subCategoryService;

	// private final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

	public ResponseEntity<CommonApiResponse> addCategory(Category category, MultipartFile image) {

		CommonApiResponse response = new CommonApiResponse();
		try {
			if (category == null) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Category data = null;
			Boolean flag = true;
			Category cat = new Category();
			List<Category> lst = categoryRepository.findAll();// List of Categories
			for (Category name : lst) {
				if (name.getName().equals(category.getName())) {
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
				response.setResponseMessage("Category Added successfully:----->" + data);
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

	// Method to Update Category

	public ResponseEntity<CommonApiResponse> updateCategoryDetails(Category category) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (category == null) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Category categ = categoryRepository.findById(category.getId()).get();

			if (categ == null) {
				response.setResponseMessage("Category Not Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			categ.setName(category.getName());
			categ.setDescription(category.getDescription());
			categ.setStatus(CategoryStatus.ACTIVE.value());
			categ.setImage(categ.getImage());
			Category savedCategory = categoryRepository.save(categ);

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

	// Method to Update Category Image

	public ResponseEntity<CommonApiResponse> updateCategoryImage(Long id, MultipartFile image) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			if (image == null) {
				response.setResponseMessage("Image is Not Selected");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
				response.setResponseMessage("Failed to update category");
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

	// Method To Fetch All Categories

	public ResponseEntity<CategoryResponseDto> fetchAllCategory() {
		CategoryResponseDto response = new CategoryResponseDto();
		try {
			List<Category> categories = getAllCategoriesByStatusIn(Arrays.asList(CategoryStatus.ACTIVE.value()));
			if (CollectionUtils.isEmpty(categories)) {

				response.setResponseMessage("No Category found");
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

	private List<Category> getAllCategoriesByStatusIn(List<String> status) {
		return categoryRepository.findByStatusIn(status);
	}

	// Method To Delete Category

	public ResponseEntity<CommonApiResponse> deleteCategory(Long categoryId) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (categoryId == null) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			Optional<Category> categ = categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE.value());
			if (categ.isEmpty()) {
				response.setResponseMessage("Category not found, failed to delete the Category");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			Category category = categ.get();

			List<SubCategory> subc = new ArrayList<>();

			subc = subCategoryService.getAllSubCategoriesByCategoryAndStatusIn(category,
					Arrays.asList(SubCategoryStatus.ACTIVE.value()));

			category.setStatus(CategoryStatus.DEACTIVATED.value());

			Category deletedCategory = categoryRepository.save(category);

			if (!subc.isEmpty()) {
				for (SubCategory subCategory : subc) {
					subCategory.setStatus(SubCategoryStatus.DEACTIVATED.value());
				}
			}

			if (deletedCategory == null) {
				response.setResponseMessage("Category is Not Deleted..");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			response.setResponseMessage("Category Deleted Successful");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to fetch CategoryImage by ImageName

	public ResponseEntity<byte[]> fetchCategoryImage(String CategoryImageName) {
		try {
			Resource resource = storageService.load(CategoryImageName);

			if (resource != null && resource.exists()) {
				try (InputStream in = resource.getInputStream()) {
					byte[] imageBytes = IOUtils.toByteArray(in);

					// Detect the image type by inspecting its content
					String imageFormat = getImageFormat(imageBytes);

					// Set content type dynamically based on detected image format
					MediaType mediaType = MediaType.parseMediaType("image/" + imageFormat.toLowerCase());
					ResponseEntity<byte[]> imageResponseEntity = CustomerUtils.getImageResponseEntity(imageBytes,
							mediaType);
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

	// Method to detect the image format based on its content
	private String getImageFormat(byte[] imageBytes) throws IOException {
		try (InputStream is = new ByteArrayInputStream(imageBytes);
				ImageInputStream imageInputStream = ImageIO.createImageInputStream(is)) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				return reader.getFormatName();
			}
		}
		throw new IOException("Unknown image format");
	}

	public ResponseEntity<CategoryResponseDto> findById(Long id) {
		CategoryResponseDto response = new CategoryResponseDto();
		try {
			Optional<Category> cate = categoryRepository.findByIdAndStatus(id, CategoryStatus.ACTIVE.value());
			if (cate.isEmpty()) {
				response.setResponseMessage("No Category found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			response.setResponseMessage("Category Fetched Sucessfully");
			response.setSuccess(true);
			response.setCategory(cate.get());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<CommonApiResponse> deleteAllCategory() {
		CommonApiResponse response = new CommonApiResponse();
		try {
			List<Category> lst = categoryRepository.findAll();
			if (!lst.isEmpty()) {
				categoryRepository.deleteAll();
				storageService.deleteAll();
				response.setResponseMessage("All Categories Deleted Successful");
				response.setSuccess(true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setResponseMessage("No Categories are Present To Delete");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Method to fetch Category by Category Name

	public ResponseEntity<CategoryResponseDto> findByName(String name) {

		CategoryResponseDto response = new CategoryResponseDto();
		try {
			List<Category> categories = findByNameContainingIgnoreCaseAndStatusIn(name,
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

	private List<Category> findByNameContainingIgnoreCaseAndStatusIn(String name, List<String> status) {

		return categoryRepository.findByNameContainingIgnoreCaseAndStatusIn(name, status);
	}

	public ResponseEntity<CommonApiResponse> statusUpdate(Long id) {
		CommonApiResponse response = new CommonApiResponse();
		try {
			if (id == 0) {
				response.setResponseMessage("missing input");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			Optional<Category> cat = categoryRepository.findById(id);

			if (cat.isEmpty()) {
				response.setResponseMessage("No User Found");
				response.setSuccess(false);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			Category ad = cat.get();
			if (ad.getStatus().contains(CategoryStatus.ACTIVE.value())) {
				ad.setStatus(CategoryStatus.DEACTIVATED.value());
			} else {
				ad.setStatus(CategoryStatus.ACTIVE.value());
			}
			categoryRepository.save(ad);
			response.setResponseMessage("Status " + ad.getStatus() + " sucessfully");
			response.setSuccess(true);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setResponseMessage("SOMETHING_WENT_WRONG");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
