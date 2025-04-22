#!/bin/bash

# Create replication user
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE ROLE replicator WITH REPLICATION LOGIN PASSWORD 'postgres';
  SELECT pg_create_physical_replication_slot('replication_slot');
EOSQL


# Modify pg_hba.conf to allow replication connections from the Docker network
echo "host replication all 0.0.0.0/0 md5" >> "$PGDATA/pg_hba.conf"

# Modify postgresql.conf for replication
echo "wal_level = replica" >> "$PGDATA/postgresql.conf"
echo "max_wal_senders = 10" >> "$PGDATA/postgresql.conf"
echo "wal_keep_size = 64MB" >> "$PGDATA/postgresql.conf"
echo "synchronous_commit = off" >> "$PGDATA/postgresql.conf"
echo "synchronous_standby_names = ''" >> "$PGDATA/postgresql.conf"
echo "listen_addresses = '*'" >> "$PGDATA/postgresql.conf"

# Reload PostgreSQL to apply the new configuration
pg_ctl reload