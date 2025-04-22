package com.am.study.db.springbootmultiinstancesdb.adapters.core.configs.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari.rw")
    public HikariConfig rwDataSourceProperties() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari.ro")
    public HikariConfig roDataSourceProperties() {
        return new HikariConfig();
    }

    @Bean("rwDataSource")
    public DataSource rwDataSource(@Qualifier("rwDataSourceProperties") final HikariConfig writeConfiguration) {
        return new HikariDataSource(writeConfiguration);
    }

    @Bean("roDataSource")
    public DataSource roDataSource(@Qualifier("roDataSourceProperties") final HikariConfig readConfiguration) {
        return new HikariDataSource(readConfiguration);
    }

    @Bean("dataSource")
    public DataSource dataSource(@Qualifier("rwDataSource") final DataSource writeConfiguration,
                                 @Qualifier("roDataSource") final DataSource readConfiguration) {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DbContextHolder.DbType.RW, rwDataSource((HikariConfig) writeConfiguration));
        dataSourceMap.put(DbContextHolder.DbType.RO, roDataSource((HikariConfig) readConfiguration));

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(roDataSource((HikariConfig) readConfiguration));

        return routingDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}