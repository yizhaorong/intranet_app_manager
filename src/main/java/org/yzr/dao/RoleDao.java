package org.yzr.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.yzr.model.Role;

import java.util.List;

public interface RoleDao extends CrudRepository<Role, String> {
    @Query("select r from Role r where r.name=:name")
    List<Role> findByName(@Param("name") String name);
}
