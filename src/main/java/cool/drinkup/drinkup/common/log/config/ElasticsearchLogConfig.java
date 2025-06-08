package cool.drinkup.drinkup.common.log.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch日志配置类
 * 启用ES Repository并指定扫描包路径
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "cool.drinkup.drinkup.common.log.repository.impl")
public class ElasticsearchLogConfig {
    
    // 其他ES相关的配置bean可以在这里定义
    // 目前使用Spring Boot的自动配置即可
} 