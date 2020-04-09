package org.siu.myboot.auth.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * 常量
 *
 * @Author Siu
 * @Date 2020/2/26 11:02
 * @Version 0.0.1
 */
public class Constant {


    /**
     * redis key
     */
    public static class RedisKey {

        /**
         * 用于认证授权版本号
         */
        public static final String USER_AUTH_KEY = "AKAGI:AUTH:USER_AUTH_VERSION:";
        public static final String USER_TOKEN_SECRET_KEY = "AKAGI:AUTH:USER_TOKEN_SECRET:";
    }


    /**
     * 认证、授权常量定义
     */
    public static class Auth {

        /**
         * token secret 属性分割符
         */
        public static final String BASE64_SECRET_SPLIT = ":";

        /**
         * token base64 secret
         */
        public static final String JSON_WEB_TOKEN_BASE64_SECRET = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";


        /**
         * jwt刷新权限标识
         */
        public static final String JSON_WEB_TOKEN_REFRESH_PERMIT = "SYS:REFRESH_TOKEN";

        /**
         * 请求头token参数
         */
        public static final String AUTHORIZATION_HEADER = "Authorization";

        /**
         * token 前缀
         */
        public static final String TOKEN_PREFIX = "Bearer ";

        /**
         * token中权限信息key
         */
        public static final String AUTHORITIES_KEY = "auth";
        public static final String ORIGIN_AUTHORITIES_KEY = "o_auth";

        /**
         * token用户的版本信息key
         */
        public static final String VERSION_KEY = "version";
        /**
         * token中权限信息分割符
         */
        public static final String AUTHORITIES_SPLIT = ",";

        /**
         * 刷新token临界值（毫秒）
         */
        public static final int REFRESH_TOKEN_TIME_THRESHOLD_MS = 30 * 60 * 1000;

        /**
         * 每次刷新token最小增加续租时间（毫米）
         */
        public static final int REFRESH_TOKEN_RENEW_TIME_MS = 60 * 60 * 1000;


        /**
         * 无需权限的接口
         */
        public static final Set<String> PERMIT_ALL_API = new HashSet<String>() {
            {
                // add("/config/get");
                // add("/echo");
            }
        };


    }

}
