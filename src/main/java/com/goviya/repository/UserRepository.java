package com.goviya.repository;

import com.goviya.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByPhone(String phone);
    List<User> findByRole(String role);
    List<User> findByRoleAndDistrict(String role, String district);
    List<User> findByRoleAndFcmTokenIsNotNull(String role);
    List<User> findByRoleInAndDistrict(List<String> roles, String district);
}
