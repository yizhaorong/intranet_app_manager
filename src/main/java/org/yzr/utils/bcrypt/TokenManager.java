package org.yzr.utils.bcrypt;

import com.qcloud.cos.utils.Md5Utils;


public class TokenManager {
    // 秘钥
    static final String SECRET = "X-app-manager-Token";

    /**
     * 生成token
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static String generateToken(String username, String password) {
        return Md5Utils.md5Hex(username + "&&" + password + "&&" + SECRET);
    }

}
