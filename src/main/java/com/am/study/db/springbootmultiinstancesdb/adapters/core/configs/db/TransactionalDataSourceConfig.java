package com.am.study.db.springbootmultiinstancesdb.adapters.core.configs.db;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Component
@EnableTransactionManagement
public class TransactionalDataSourceConfig {

    private final DataSourceConfig databaseConfig;

    public TransactionalDataSourceConfig(DataSourceConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource) {
            @Override
            protected void doBegin(Object transaction, TransactionDefinition definition) {
                // Auto-set context based on @Transactional settings
                DbContextHolder.setDbType(
                        definition.isReadOnly() ? DbContextHolder.DbType.RO : DbContextHolder.DbType.RW
                );
                super.doBegin(transaction, definition);
            }

            @Override
            protected void doCleanupAfterCompletion(Object transaction) {
                super.doCleanupAfterCompletion(transaction);
                DbContextHolder.clearDbType();
            }
        };
    }
}
