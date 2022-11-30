package com.qkm.xinguan.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qiukangming
 * @version 1.0
 * @description
 * @since 2020/10/13 14:54
 */

@Data
@Component //添加到Ioc容器中
@ConfigurationProperties(prefix = "alioss")
public class OssEntity {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String urlPrefix;
}
