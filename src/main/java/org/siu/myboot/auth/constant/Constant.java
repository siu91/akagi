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


    public static final String UNKNOWN = "unknown";



    /**
     * redis key
     */
    public static class RedisKey {

        /**
         * 用于认证授权版本号
         */
        public static final String USER_AUTH_KEY = "AKAGI:AUTH:USER_AUTH_VERSION:";
    }


    /**
     * 认证、授权常量定义
     */
    public static class Auth {
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
         * 无需token校验的接口
         */
        public static final String PERMIT_ALL_API1 = "/v1/api/auth";



        /**
         * 无需权限的接口
         */
        public static final Set<String> PERMIT_ALL_API = new HashSet<String>() {
            {
                //add(PERMIT_ALL_API1);
               // add(PERMIT_ALL_API2);
               // add(PERMIT_ALL_API10);
                // 测试用
                add("/config/get");
                add("/echo");
            }
        };

        /**
         * 无需校验token
         */
        public static final Set<String> NO_CHECK_API = new HashSet<String>() {
            {
                add(PERMIT_ALL_API1);


            }
        };

        /**
         * 不拦截工具的API
         * swagger、eureka、spring boot admin
         */
        public static final Set<String> NO_CHECK_TOOLS_API = new HashSet<String>() {
            {
                add("swagger");
                add("eureka");
                add("actuator");

            }
        };


    }

}
