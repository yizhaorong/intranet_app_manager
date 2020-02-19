package org.yzr.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.yzr.model.Permission;
import org.yzr.model.Role;
import org.yzr.model.User;
import org.yzr.service.UserService;
import org.yzr.utils.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        User user = (User) getAvailablePrincipal(principals);
        Set<String> roles = toRoles(user.getRoleList());
        Set<String> permissions = toPermissions(user.getRoleList());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    private Set<String> toRoles(Set<Role> roleList) {
        Set<String> roles = new HashSet<>();
        for (Role role : roleList) {
            roles.add(role.getName());
        }
        return roles;
    }

    private Set<String> toPermissions(Set<Role> roleList) {
        Set<String> permissions = new HashSet<>();
        for (Role role : roleList) {
            for (Permission permission : role.getPermissionList()) {
                permissions.add(permission.getPermission());
            }
        }
        return permissions;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (StringUtils.isEmpty(username)) {
            throw new AccountException("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new AccountException("密码不能为空");
        }

        User user = userService.login(username, password);
        Assert.state(user != null, "用户名或密码错误");
        if (user == null) {
            throw new UnknownAccountException("用户名或密码错误");
        }
        return new SimpleAuthenticationInfo(user, password, getName());
    }

}