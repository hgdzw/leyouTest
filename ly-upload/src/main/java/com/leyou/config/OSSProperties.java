package com.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: dzw
 * @Date: 2019/6/14 9:40
 * @Version 1.0
 */
@Component
@Data
@ConfigurationProperties("ly.oss")
public class OSSProperties {

    private String accessKeyId;
    private String accessKeySecret;
    private String bucket;
    private String host;
    private String endpoint;
    private String dir;
    private long expireTime;
    private long maxFileSize;
}
