# Hibernate ORM Demo

Learn and play with Hibernate to map Java objects and PostgreSQL data

Implemented based on LinkedIn learning course:
[Java Persistence with JPA and Hibernate](https://www.linkedin.com/learning/java-persistence-with-jpa-and-hibernate)

Tech stack
- JPA
- Hibernate
- PostgreSQL

### Prepare

- Install PostgreSQL Server on your local machine
- Install PostgreSQL Client: `pgAdmin` desktop app or `psql` console tool
- Connect to PostgreSQL Server by any client
- Create `hibernate` database to be used for this demo and switch to it
- Execute queries to create `jpa_hibernate`schema in `hibernate` database 

```
CREATE SCHEMA IF NOT EXISTS jpa_hibernate
    AUTHORIZATION postgres;
);
```
 add tables in PostgreSQL database:

### Practice: JPA implementation with Hibernate

Having stand-alone Java application (running without web server) we will have next Gradle dependencies:


