package com.digitalcoin.holdings_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 应用程序全局配置类
 */
@Configuration
public class ApplicationConfig {

    /**
     * 配置跨域请求
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Content-Length", "Content-Type")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    /**
     * 配置全局日期时间格式化（可选）
     */
    // 这里可以添加日期格式化、消息转换器等全局配置
}