package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<Profile,String> {

}
