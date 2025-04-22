package com.am.study.db.springbootmultiinstancesdb.adapters.repositories;

import org.springframework.jdbc.core.JdbcTemplate;

public interface DataSourceInspector {

    default String getDataSourceName(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForObject(
                "SELECT current_setting('application_name')", String.class);
    }
}
