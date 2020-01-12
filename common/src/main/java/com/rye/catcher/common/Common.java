package com.rye.catcher.common;

/**
 * Created
 */
public class Common {
    /**
     * 一些不可变的参数，通常用于配置
     */
    public interface  Constance{//interface下的变量都是final的
        //手机号正则
        String REGEX_MOBILE="[1][3,4,5,7,8][0-9]{9}$";
        // TODO: 2020/1/5 不要忘了替换本机ip地址--- 
        String API_URL="http://192.168.43.237:8080/api/";
    }
}
