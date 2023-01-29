# Use arm64v8/postgres as the base image
FROM arm64v8/postgres:latest

# Install the PostgreSQL client
RUN apt-get update && apt-get install -y postgresql-client

# Set up the database
RUN mkdir -p /var/lib/postgresql/data && chown -R postgres /var/lib/postgresql/data
ENV PGDATA=/var/lib/postgresql/data

# Expose the PostgreSQL port
EXPOSE 5432

# Start the PostgreSQL server
CMD ["postgres"]


ENV POSTGRES_USER=test
ENV POSTGRES_PASSWORD=test
ENV POSTGRES_DB=payments

RUN  /etc/init.d/postgresql start &&\
    psql --command "CREATE USER ${POSTGRES_USER} WITH SUPERUSER PASSWORD '${POSTGRES_PASSWORD}';" &&\
    createdb -O ${POSTGRES_USER} ${POSTGRES_DB}
