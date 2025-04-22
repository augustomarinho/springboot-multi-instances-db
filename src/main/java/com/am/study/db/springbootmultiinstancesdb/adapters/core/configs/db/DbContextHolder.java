package com.am.study.db.springbootmultiinstancesdb.adapters.core.configs.db;

public class DbContextHolder {
    private static final ThreadLocal<DbType> contextHolder = new ThreadLocal<>();

    public enum DbType {
        RW, RO
    }

    public static void setDbType(DbType dbType) {
        if (dbType == null) {
            throw new NullPointerException();
        }
        contextHolder.set(dbType);
    }

    public static DbType getDbType() {
        return contextHolder.get() == null ? DbType.RO : contextHolder.get();
    }

    public static void clearDbType() {
        contextHolder.remove();
    }
}
