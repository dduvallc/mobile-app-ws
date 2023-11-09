package com.dduvall.developerblog.apps.ws;

import com.dduvall.developerblog.apps.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

    /*
        If I were not using spring data JPA, I would most likely have to create a data access object class
        and then for each crude operation like create, read, update and delete, I'll have to create a separate
        method and then write business logic that will open database connection and then perform SQL query and
        then close the database connection.
        Or I'll have to add methods that use hibernate.
        And again, for each operation I would have to create a separate method that creates or reads or updates
        records. With Spring Data JPA It is all much, much easier.

        Instead of creating data access object, we simply create user repository and then we make it extend
        the Crud repository which enables us to call a ready to use methods like save user details is already
        done for us. All we need to do is to call the method which has been provided to us
         and give it a user entity object
     */

@Repository
//public interface UserRepository extends CrudRepository<UserEntity, Long> {
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>, ListCrudRepository<UserEntity, Long> { //CrudRepository, above, will still work

        UserEntity findByEmail(String email);

        UserEntity findByUserId(String userId);

}
