#!/bin/bash
set -e
echo "Restoring old DB dump..."

# certdb.dump lo formatnya custom (-Fc), jadi pake pg_restore
pg_restore -U "$POSTGRES_USER" -d "$POSTGRES_DB" /docker-entrypoint-initdb.d/certdb.dump

echo "Restore completed!"