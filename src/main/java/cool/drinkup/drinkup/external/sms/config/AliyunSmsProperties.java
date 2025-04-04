package cool.drinkup.drinkup.external.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import cool.drinkup.drinkup.external.sms.SmsSender;
import cool.drinkup.drinkup.external.sms.impl.AliyunSmsSender;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliyunSmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
    private String endpoint = "https://dysmsapi.aliyuncs.com";
    private int maxRetries = 3;
    private long verificationCodeExpireMinutes = 5;

    @Bean
    SmsSender smsSender(AliyunSmsProperties properties, ObjectMapper objectMapper, RedisTemplate<String, String> redisTemplate) {
        return new AliyunSmsSender(properties, objectMapper, redisTemplate);
    }
} 