package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.User;
import com.carl.ms_security.Models.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {
    List<UserRole> findByUser(User user);
}
