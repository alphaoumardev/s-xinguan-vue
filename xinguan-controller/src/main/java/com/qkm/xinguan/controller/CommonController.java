package com.qkm.xinguan.controller;

import com.qkm.xinguan.response.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiukangming
 * @version 1.0
 * @date 2021/03/29 16:57
 * @description
 */

@RestController
public class CommonController implements ICommonController {

    private final Environment environment;

    public CommonController(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Result getDruidLoginConfig() {
        Map<String, String> config = new HashMap<>(2);
        config.put("username", environment.getProperty("druid.username"));
        config.put("password", environment.getProperty("druid.password"));
        return Result.ok().data(config);
    }

    @Override
    public Result getSystemAuthorInfo() {
        Map<String, String> data = new HashMap<>(4);
        data.put("author", environment.getProperty("system.author"));
        data.put("phone", environment.getProperty("system.phone"));
        data.put("qq", environment.getProperty("system.qq"));
        data.put("wechat", environment.getProperty("system.wechat"));
        return Result.ok().data(data);
    }
}
