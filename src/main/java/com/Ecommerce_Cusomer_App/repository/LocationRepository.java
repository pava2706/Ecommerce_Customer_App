package com.Ecommerce_Cusomer_App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce_Cusomer_App.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
	// Define custom queries if needed
}
