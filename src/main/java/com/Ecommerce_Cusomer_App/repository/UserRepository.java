package com.Ecommerce_Cusomer_App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce_Cusomer_App.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByPhoneNumber(String phoneNumber);

	Optional<User> findByEmail(String email);
}
