#!/bin/sh
set -e

# Cloud hosts (Render, Railway, Heroku, …) provide the DB as a single DATABASE_URL of the form
#   postgresql://user:password@host:port/dbname
# Spring needs a JDBC URL plus separate credentials, so convert it here when present.
# When DATABASE_URL is absent (e.g. docker-compose, which sets SPRING_DATASOURCE_* directly),
# this block is skipped and the existing env vars are used as-is.
if [ -n "$DATABASE_URL" ]; then
  no_proto="${DATABASE_URL#*://}"        # user:password@host:port/db
  creds="${no_proto%@*}"                 # user:password
  hostpart="${no_proto#*@}"              # host:port/db
  export SPRING_DATASOURCE_USERNAME="${creds%%:*}"
  export SPRING_DATASOURCE_PASSWORD="${creds#*:}"
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${hostpart}"
fi

exec java -jar /app/app.jar
