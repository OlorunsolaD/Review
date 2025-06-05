package com.reviewyme.userservice.existinsetup.repository;

import com.reviewyme.userservice.existinsetup.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository <UserEntity,String>{

    Optional<UserEntity> findByEmail(String email);
//    Optional<UserEntity> findUserStatusById (String id);
    Optional<UserEntity> findUserById(String id);
    Optional<UserEntity> findByUserId(String userId);

}
