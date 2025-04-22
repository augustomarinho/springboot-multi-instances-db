package com.am.study.db.springbootmultiinstancesdb.application.ports;

import com.am.study.db.springbootmultiinstancesdb.application.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPort {
    User save(User user);
    Optional<User> findByIdForceDBType(Long id, String dbType);
    Optional<User> findByIdWithTransactionRO(Long id);
    Optional<User> findByIdWithTransactionRW(Long id);
    Optional<User> findByExternalIdForceDBType(UUID externalId, String dbType);
    List<User> findAllForceDBType(String dbType);
    void deleteById(Long id);
}
