package com.am.study.db.springbootmultiinstancesdb.adapters.core.configs.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionRoutingDataSource extends AbstractRoutingDataSource {

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        // Default to RO if no transaction is active
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            return DbContextHolder.DbType.RO;
        }
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
                DbContextHolder.DbType.RO :
                DbContextHolder.DbType.RW;
    }
}
