package org.yzr.service;


import org.springframework.stereotype.Service;
import org.yzr.dao.RoleDao;
import org.yzr.model.Role;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
public class RoleService {
    @Resource
    private RoleDao roleDao;

    @Transactional
    public Role save(Role role) {
        return this.roleDao.save(role);
    }

}
