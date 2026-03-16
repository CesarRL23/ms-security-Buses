package com.carl.ms_security.Repositories;

import com.carl.ms_security.Models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository  extends MongoRepository<Session,String> {
}
