package org.yzr.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.yzr.model.User;

public interface UserDao extends CrudRepository<User, String> {

    @Query("select u from User u where u.username=:username and u.password=:password")
    public User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Query("select u from User u where u.username=:username")
    public User findByUsername(@Param("username") String username);

    @Override
    @Query("select u from User u order by u.createTime desc ")
    Iterable<User> findAll();

    @Query("select u from User u where u.token=:token")
    User findByToken(@Param("token") String token);
}
