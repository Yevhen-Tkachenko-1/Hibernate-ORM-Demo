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

### Practice: JPA implementation with Hibernate

#### Project setup:

Having stand-alone Java application (running without web server) we will have next Gradle dependencies:

```
dependencies {
    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core
    implementation("org.hibernate.orm:hibernate-core:6.6.1.Final")
    
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.4")
}
```

Next, we should have Hibernate properties in `persistance.xml` file
under `src/main/resources/META-INF` directory like this:

```
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="3.0" xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">
    <!-- Define Persistence Unit -->
    <persistence-unit name="jpa_and_hibernate" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver" />
            <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/hibernate" />
            <property name="jakarta.persistence.jdbc.user" value="${env.POSTGRESQL_USER_NAME}" />
            <property name="jakarta.persistence.jdbc.password" value="${env.POSTGRESQL_USER_PASSWORD}" />
            <property name="hibernate.show_sql" value="true" />
        </properties>
    </persistence-unit>
</persistence>
```

Make sure you have env variables on your machine with keys `POSTGRESQL_USER_NAME`, `POSTGRESQL_USER_PASSWORD`.

Now, we are ready to implement JPA layer.

#### Challenge: Define "Art School" schema in PostgreSQL

**Task**:
- Create schema `art_school` in `hibernate` database.
- Create table `art_teacher` with fields `id` as primary key, `name`.
- Create table `art_student` with fields `id` as primary key, `name`.
- Create table `art_class` with fields `id` as primary key, `name`, `week day`,
  and `teacher_id` as reference to `teacher` table.
- Create mapping for `art_student` and `art_class` tables as many-to-many relation.
- Create table `review` with fields `id`, `comment`, `rating`,
  and `teacher_id` as reference to `teacher` table.

**Solution**:
```
CREATE SCHEMA IF NOT EXISTS art_school
    AUTHORIZATION postgres;
```
```
CREATE TABLE IF NOT EXISTS art_school.art_teachers(
    teacher_id   serial,
    teacher_name varchar(255),
    
    PRIMARY KEY (teacher_id)
);
```
```
CREATE TABLE IF NOT EXISTS art_school.art_students(
    student_id   serial,
    student_name varchar(255),
    
    PRIMARY KEY (student_id)
);
```
```
CREATE TABLE IF NOT EXISTS art_school.art_classes(
    class_id    serial,
    class_name  varchar(255),
    week_day    varchar(255),
    theacher_id int,

    PRIMARY KEY (class_id),
    FOREIGN KEY (theacher_id) REFERENCES art_school.art_teachers(teacher_id) 
        MATCH SIMPLE 
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);
```
```
CREATE TABLE IF NOT EXISTS art_school.students_classes_mapping(
    student_id int,
    class_id   int,
    
    PRIMARY KEY (student_id, class_id),
    FOREIGN KEY (student_id) REFERENCES art_school.art_students(student_id) 
        MATCH SIMPLE 
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    FOREIGN KEY (class_id)   REFERENCES art_school.art_classes(class_id)
        MATCH SIMPLE 
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);
```
```
CREATE TABLE IF NOT EXISTS art_school.art_reviews(
    review_id      serial,
    review_comment varchar(255),
    rating         int,
    teacher_id     int,
    
    PRIMARY KEY (review_id),
    FOREIGN KEY (teacher_id) REFERENCES art_school.art_teachers(teacher_id)
        MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);
``` 


