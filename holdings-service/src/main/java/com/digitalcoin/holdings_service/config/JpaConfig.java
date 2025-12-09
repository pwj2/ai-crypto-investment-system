package com.digitalcoin.holdings_service.config;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA配置类
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.digitalcoin.holdings_service.repository")
@EnableTransactionManagement
@EnableConfigurationProperties(JpaProperties.class)
public class JpaConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(JpaConfig.class);

    /**
     * 配置实体管理器工厂
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaProperties jpaProperties) {
        logger.info("开始配置实体管理器工厂");
        logger.info("数据源类型: {}", dataSource.getClass().getName());
        
        // 测试数据源连接
        try (Connection conn = dataSource.getConnection()) {
            logger.info("数据库连接测试成功!");
            logger.info("数据库URL: {}", conn.getMetaData().getURL());
            logger.info("数据库产品名称: {}", conn.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            logger.error("数据库连接测试失败!", e);
        }
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.digitalcoin.holdings_service.entity");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        // 设置JPA属性
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        factory.setJpaPropertyMap(properties);
        
        logger.info("实体管理器工厂配置完成");
        return factory;
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}