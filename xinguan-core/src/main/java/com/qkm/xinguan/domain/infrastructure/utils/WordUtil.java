package com.qkm.xinguan.domain.infrastructure.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/04 23:24
 * @description 字符检测工具类
 */
public class WordUtil {
    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        return m.find();
    }
}
