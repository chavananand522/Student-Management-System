package com.itvedant.StudentManagement.reposatory;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itvedant.StudentManagement.model.Users;

public interface UserRepository extends JpaRepository<Users,Long> {
	
	boolean existsByUserName(String userName);
	
	Optional<Users> findByUserName(String userName);
}
