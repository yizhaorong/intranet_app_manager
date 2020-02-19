package org.yzr.dao;

import org.springframework.data.repository.CrudRepository;
import org.yzr.model.Permission;
import org.yzr.model.User;

public interface PermissionDao extends CrudRepository<Permission, String> {

}
