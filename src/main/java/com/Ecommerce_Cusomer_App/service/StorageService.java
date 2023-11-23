package com.Ecommerce_Cusomer_App.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface StorageService {

	List<String> loadAll();

	String store(MultipartFile file);

	Resource load(String fileName);

	void delete(String fileName);

	void deleteAll();

}
