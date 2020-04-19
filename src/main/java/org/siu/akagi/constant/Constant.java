package org.siu.akagi.constant;

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
        public static final String USER_TOKEN_SECRET_KEY = "AKAGI:AUTH:UTSK:";
    }


    /**
     * 认证、授权常量定义
     */
    public static class Auth {

        /**
         * 默认 refresh token 失效时间
         */
        public static final long DEFAULT_REFRESH_TOKEN_EXPIRE_MS = 60 * 60 * 24 * 7 * 1000;

        /**
         * Token Sign 属性分割符
         */
        public static final String BASE64_SECRET_SPLIT = ":";

        /**
         * token base64 secret
         */
        public static final String JSON_WEB_TOKEN_BASE64_SECRET = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";


        /**
         * jwt刷新权限标识
         */
        public static final String JSON_WEB_TOKEN_REFRESH_PERMIT = "AKAGI:SYS:RT";

        /**
         * 请求头token参数
         */
        public static final String AUTHORIZATION_HEADER = "Authorization";

        /**
         * token 前缀
         */
        public static final String TOKEN_PREFIX = "Bearer ";
        /**
         * token 分割符
         */
        public static final String TOKEN_SPLIT = ".";
        public static final String TOKEN_SPLIT_REGEX = "\\.";
        public static final int TOKEN_SPLIT_LENGTH = 4;

        /**
         * token中权限信息key
         */
        public static final String AUTHORITIES_KEY = "auth";
        public static final String ORIGIN_AUTHORITIES_KEY = "o_auth";

        /**
         * token 换取的ID
         */
        public static final String TOKEN_ID_KEY = "ti";

        /**
         * token中权限信息分割符
         */
        public static final String AUTHORITIES_SPLIT = ",";

        /**
         * 无需权限的接口
         */
        public static final Set<String> PERMIT_ALL_API = new HashSet<String>() {
            {
                add("/error");
            }
        };


    }

}
