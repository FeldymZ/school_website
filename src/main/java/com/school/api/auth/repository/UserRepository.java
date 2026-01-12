package com.school.api.auth.repository;

import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  List<User> findByRole(Role role);

  List<User> findByEnabled(Boolean enabled);

  List<User> findByRoleAndEnabled(Role role, Boolean enabled);

  //  Recherche LIKE
  List<User> findByEmailContainingIgnoreCase(String email);
}
