package com.itvedant.StudentManagement.reposatory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itvedant.StudentManagement.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserName(String userName);

    Optional<Users> findByUserNameIgnoreCase(String userName);

    /**
     * Returns the FIRST match — safe when duplicates exist.
     */
    Optional<Users> findFirstByUserNameIgnoreCase(String userName);

    Optional<Users> findByEmail(String email);

    /**
     * Returns the FIRST match — safe when duplicates exist.
     */
    Optional<Users> findFirstByEmail(String email);

    boolean existsByUserNameIgnoreCase(String userName);

    boolean existsByEmail(String email);
}