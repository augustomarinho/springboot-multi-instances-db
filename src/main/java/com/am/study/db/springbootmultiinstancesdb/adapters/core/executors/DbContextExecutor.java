package com.am.study.db.springbootmultiinstancesdb.adapters.core.executors;

import com.am.study.db.springbootmultiinstancesdb.adapters.core.configs.db.DbContextHolder;

import java.util.function.Supplier;

public class DbContextExecutor {
    public static <T> T executeWithDbContext(DbContextHolder.DbType dbType, Supplier<T> action) {
        DbContextHolder.setDbType(dbType);
        try {
            return action.get();
        } finally {
            DbContextHolder.clearDbType();
        }
    }
}