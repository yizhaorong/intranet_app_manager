package org.yzr.service;


import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yzr.dao.PermissionDao;
import org.yzr.dao.RoleDao;
import org.yzr.dao.UserDao;
import org.yzr.model.Permission;
import org.yzr.model.Role;
import org.yzr.model.User;
import org.yzr.utils.bcrypt.BCryptPasswordEncoder;
import org.yzr.utils.bcrypt.TokenManager;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private PermissionDao permissionDao;
    @Resource
    private RoleDao roleDao;
    @Resource
    private Environment environment;

    @Transactional
    public User save(User user) {
        updateToken(user);
        return this.userDao.save(user);
    }

    /**
     * 更新token
     *
     * @param user
     */
    private void updateToken(User user) {
        String token = user.getToken();
        User tokenUser = this.userDao.findByToken(token);
        if (tokenUser == null) {
            token = TokenManager.generateToken(user.getUsername(), user.getPassword());
        } else {
            if (!(user.getUsername().equals(tokenUser.getUsername())
                    && user.getPassword().equals(tokenUser.getPassword()))) {
                token = TokenManager.generateToken(user.getUsername(), user.getPassword());
            }
        }
        user.setToken(token);
    }

    @Transactional
    public List<User> findAll() {
        Iterable<User> users = this.userDao.findAll();
        List<User> list = new ArrayList<>();
        for (User user : users) {
            list.add(user);
        }
        return list;
    }

    @Transactional
    public User login(String username, String password) {
        User user = this.userDao.findByUsername(username);
        if (user != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(password, user.getPassword())) return null;
            // 级联查询
            user.getRoleList().forEach(role -> {
                role.getPermissionList().forEach(permission -> {
                });
            });
        }
        return user;
    }

    @Transactional
    public User findByToken(String token) {
        if (StringUtils.isEmpty(token)) return null;
        return this.userDao.findByToken(token);
    }

    @Transactional
    public void deleteById(String id) {
        User user = this.userDao.findById(id).get();
        if (user != null) {
            this.userDao.deleteById(id);
        }
    }

    @Transactional
    public void updateById(User user) {
        this.userDao.save(user);
    }

    @Transactional
    public User createUser(String username, String password) {
        User user = new User();
        user.setEnable(true);
        user.setUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(password));
        user.setCreateTime(System.currentTimeMillis());

        Role role = this.roleDao.findByName("管理员");
        Set<Role> roleList = new HashSet<>();
        roleList.add(role);
        user.setRoleList(roleList);
        updateToken(user);
        this.userDao.save(user);
        return user;
    }

    @Transactional
    public void initUsers() {
        String username = environment.getProperty("admin.username");
        String password = environment.getProperty("admin.password");
        if (this.userDao.findByUsername(username) == null) {
            long currentTime = System.currentTimeMillis();
            Role role = new Role();
            role.setCreateTime(currentTime);
            role.setDescription("管理员");
            role.setName("管理员");
            role.setEnabled(true);
            this.roleDao.save(role);

            List<Permission> permissionList = new ArrayList<>();
            String[] perms = new String[]{
                    "/apps",
                    "/apps/get",
//                    "/apps/delete",
                    "/packageList/get"
            };
            for (String string : perms) {
                Permission permission = new Permission();
                permission.setCreateTime(currentTime);
                permission.setPermission(string);
                permission.setUpdateTime(currentTime);
                permission.setRole(role);
                this.permissionDao.save(permission);
                permissionList.add(permission);
            }

            User user = new User();
            user.setEnable(true);
            user.setUsername(username);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(password));
            user.setCreateTime(currentTime);

            Set<Role> roleList = new HashSet<>();
            roleList.add(role);
            user.setRoleList(roleList);
            updateToken(user);
            this.userDao.save(user);
        }
    }
}
