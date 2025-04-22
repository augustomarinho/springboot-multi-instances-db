# SpringBoot Multi Instances DB

 Project to demonstrate how to use multiple databases in a Spring Boot application. 
 The main proposal is running an application to study the Spring Boot managing Read Only and Read Write databases and simulate different scenarios around transactions and data consistency.

# Notes
 The .env file is used to set the environment variables for Docker Compose. The user and password are commited in the file, but you can change them to your own values. The database names are also set in the .env file.

# Table of Contents
- [Notes](#notes)
- [Requirements](#requirements)
- [Up and Running](#up-and-running)
- [Simulating latency in Async Replication in PostgreSQL](#simulating-latency-in-async-replication-in-postgresql)
- [Endpoints](#endpoints)
  - [Create User](#create-user)
  - [Get User](#get-user)
  - [Get User by external id](#get-user-by-external-id)
  - [Get All Users](#get-all-users)
  - [Delete User](#delete-user)
- [Stopping the application](#stopping-the-application)


## Requirements
- Java 17
- Maven 3.8.6
- PostgreSQL 15.2
- Docker
- Docker Compose

# Up and Running

Before running the application, it's necessary to run docker compose to create the databases. In this docker compose file also up a pgAdmin instance to connect to the databases.

```bash

docker-compose up -d --force-recreate
```
After that, you can run the application using the following command:

```bash

./mvnw spring-boot:run
```

## Simulating latency in Async Replication in PostgreSQL

For executing the commands below, you need to connect to the PostgreSQL instances running in the docker container. You can do this using the following command accessing PgAdmin http://localhost:8711/

### Stop replication
**Go to the replica**
```sql
SELECT pg_wal_replay_pause();
```

**Check the replication status**
```sql
SELECT 
    pg_is_wal_replay_paused(),
    pg_last_wal_receive_lsn(),
    pg_last_wal_replay_lsn();
```

### Verify the replication status

**Go to the primary**
```sql
SELECT * FROM pg_replication_slots;
```

### Resume the replication
**Go to the replica**
```sql
SELECT pg_wal_replay_resume();
```

## Endpoints

### Create User
```bash

curl -X POST \
  http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "age": 30,
    "externalId": "69eba2e8-e61f-4283-b14e-86c3179d5245"
  }'
```

### Get User
Find user by id using read-write database
```bash

curl -X GET \
  http://localhost:8080/users/1:rw
```

Find user by id using read-only database
```bash

curl -X GET \
  http://localhost:8080/users/1:ro
```

Find user by id using Read Only Transaction (@Transactional(readOnly = true))
```bash

curl -X GET \
  http://localhost:8080/users/1/transaction/:ro
```

Find user by id using Read Write Transaction (@Transactional(readOnly = false))
```bash

curl -X GET \
  http://localhost:8080/users/1/transaction/:rw
```

### Get User by external id

Find user by external id using read-write database
```bash

curl -X GET \
  http://localhost:8080/users/external/69eba2e8-e61f-4283-b14e-86c3179d5245:rw
```

Find user by external id using read-only database
```bash

curl -X GET \
  http://localhost:8080/users/external/69eba2e8-e61f-4283-b14e-86c3179d5245:ro
```

### Get All Users
Find all users using read-write database
```bash

curl -X GET \
  http://localhost:8080/users/:rw
```
Find all users using read-only database
```bash

curl -X GET \
  http://localhost:8080/users/:ro
```

### Delete User
```bash

curl -X DELETE \
  http://localhost:8080/users/1
```

## Stopping the application

Ctrl + C to stop the application. To stop the docker containers, run the following command:
```bash

docker-compose down --volumes
```