package org.yzr.service;


import org.springframework.stereotype.Service;
import org.yzr.dao.PermissionDao;
import org.yzr.model.Permission;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
public class PermissionService {
    @Resource
    private PermissionDao permissionDao;

    @Transactional
    public Permission save(Permission permission) {
        return this.permissionDao.save(permission);
    }
}
