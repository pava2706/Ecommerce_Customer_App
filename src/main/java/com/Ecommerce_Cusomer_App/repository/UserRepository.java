package com.Ecommerce_Cusomer_App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce_Cusomer_App.entity.User;
import com.Ecommerce_Cusomer_App.utils.Constants.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByPhoneNumber(String phoneNumber);

	Optional<User> findByEmail(String email);

	User findByEmailAndStatus(String email, String status);

	//Optional<User> findByIdAndStatus(Long id, String value);

	List<User> findByStatusIn(List<String> asList);

	User findByIdAndStatus(Long userId, String status);

	User findUserByphoneNumberAndStatus(String phoneNumber, String status);
}
