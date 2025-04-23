package com.am.study.db.springbootmultiinstancesdb.adapters.repositories;

import com.am.study.db.springbootmultiinstancesdb.adapters.core.configs.db.DbContextHolder;
import com.am.study.db.springbootmultiinstancesdb.application.models.User;
import com.am.study.db.springbootmultiinstancesdb.application.ports.UserPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository implements UserPort, DataSourceInspector {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        DbContextHolder.setDbType(DbContextHolder.DbType.RW);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (name, age, external_id) VALUES (?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setInt(2, user.getAge());
            ps.setObject(3, user.getExternalId());
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        logger.info("[save] Current connection {}", getDataSourceName(jdbcTemplate));
        return user;
    }

    private Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(
                "SELECT id, external_id, name, age FROM users WHERE id = ?",
                new Object[]{id},
                userRowMapper());
        logger.info("[findById] Current connection {}", getDataSourceName(jdbcTemplate));
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> findByIdForceDBType(Long id, String dbType) {
        DbContextHolder.DbType dbTypeEnum = DbContextHolder.DbType.valueOf(dbType.toUpperCase());
        DbContextHolder.setDbType(dbTypeEnum);
        return findById(id);
    }

    private Optional<User> findByIdWithTransaction(Long id) {
        return findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByIdWithTransactionRO(Long id) {
        return findByIdWithTransaction(id);
    }

    @Override
    @Transactional(readOnly = false)
    public Optional<User> findByIdWithTransactionRW(Long id) {
        return findByIdWithTransaction(id);
    }

    private Optional<User> findByExternalId(UUID externalId, String dbType) {
        DbContextHolder.DbType dbTypeEnum = DbContextHolder.DbType.valueOf(dbType.toUpperCase());
        DbContextHolder.setDbType(dbTypeEnum);
        List<User> users = jdbcTemplate.query(
                "SELECT id, external_id, name, age FROM users WHERE external_id = ?",
                new Object[]{externalId},
                userRowMapper());
        logger.info("[findByExternalId] Current connection {}", getDataSourceName(jdbcTemplate));
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> findByExternalIdForceDBType(UUID externalId, String dbType) {
        return findByExternalId(externalId, dbType);
    }

    private List<User> findAll(String dbType) {
        DbContextHolder.DbType dbTypeEnum = DbContextHolder.DbType.valueOf(dbType.toUpperCase());
        DbContextHolder.setDbType(dbTypeEnum);
        logger.info("[findAll] Current connection {}", getDataSourceName(jdbcTemplate));
        return jdbcTemplate.query(
                "SELECT id, external_id, name, age FROM users",
                userRowMapper());
    }

    @Override
    public List<User> findAllForceDBType(String dbType) {
        return findAll(dbType);
    }

    @Override
    public void deleteById(Long id) {
        DbContextHolder.setDbType(DbContextHolder.DbType.RW);
        logger.info("[deleteById] Current connection {}", getDataSourceName(jdbcTemplate));
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getLong("id"),
                UUID.fromString(rs.getString("external_id")),
                rs.getString("name"),
                rs.getInt("age"));
    }
}
