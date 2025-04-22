package com.am.study.db.springbootmultiinstancesdb.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private UUID externalId = UUID.randomUUID();
    private String name;
    private int age;
}
