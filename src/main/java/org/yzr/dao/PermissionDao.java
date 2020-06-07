package org.yzr.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.yzr.model.Permission;

import java.util.List;

public interface PermissionDao extends CrudRepository<Permission, String> {
    @Query("select p from Permission p where p.permission=:permission and p.role.id=:roleId")
    List<Permission> findByPermission(@Param("permission") String permission, @Param("roleId") String roleId);
}
