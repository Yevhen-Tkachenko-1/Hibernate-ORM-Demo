## JPA implementation with Hibernate

Learn and play with Hibernate to map Java objects and PostgreSQL data

Implemented based on LinkedIn learning course:
[Java Persistence with JPA and Hibernate](https://www.linkedin.com/learning/java-persistence-with-jpa-and-hibernate)

**Tech stack**:

- Java 17
- JPA (Jakarta Persistence API)
- Hibernate
- PostgreSQL
- Gradle

**Content**:

* [Project setup](#project-setup)
    * [Prepare PostgreSQL](#prepare-postgresql)
    * [Configure project with Gradle](#configure-project-with-gradle)
    * [Persistence Unit: XML configuration](#persistence-unit-xml-configuration)
    * [Persistence Unit: Java configuration](#persistence-unit-java-configuration)
* [Hibernate Practice](#hibernate-practice)
    * [Challenge: Define Schema in PostgreSQL](#challenge-define-schema-in-postgresql)
    * [Challenge: Define Entities in Java](#challenge-define-entities-in-java)
    * [Challenge: CRUD Operations in Persistent Context](#challenge-crud-operations-in-persistent-context)
    * [Challenge: Entity Attachment in Persistent Context](#challenge-entity-attachment-in-persistent-context)
    * [Challenge: Entity Relations in Persistent Context](#challenge-entity-relations-in-persistent-context)
    * [Challenge: CRUD Operations with Queries](#challenge-crud-operations-with-queries)
    * [Challenge: Service vs Repository](#challenge-service-vs-repository)
    * [Challenge: JPA, Hibernate and PSQL Exceptions](#challenge-jpa-hibernate-and-psql-exceptions)

### Project setup

#### Prepare PostgreSQL

- Install PostgreSQL Server on your local machine
- Install PostgreSQL Client: `pgAdmin` desktop app or `psql` console tool
- Connect to PostgreSQL Server by any client
- Create `hibernate` database to be used for this demo and switch to it
- Create env variables on your machine: `POSTGRESQL_USER_NAME`, `POSTGRESQL_USER_PASSWORD`
  in order to allow java app connect to `hibernate` database
  and not to store credentials in repository

#### Configure project with Gradle

Having stand-alone Java application (running without web server) we will use next libs:

```kotlin
dependencies {
    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core
    implementation("org.hibernate.orm:hibernate-core:6.6.1.Final")

    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.4")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}
```

#### Persistence Unit: XML configuration

We should have JPA-Hibernate configuration in `persistance.xml` file
under `src/main/resources/META-INF` directory like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="3.0" xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">
    <!-- Define Persistence Unit -->
    <persistence-unit name="art_school" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>yevhent.demo.hibernate.entity.ArtStudent</class>
        <class>yevhent.demo.hibernate.entity.ArtTeacher</class>
        <class>yevhent.demo.hibernate.entity.ArtClass</class>
        <class>yevhent.demo.hibernate.entity.ArtReview</class>
        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/hibernate"/>
            <property name="jakarta.persistence.jdbc.user" value="${env.POSTGRESQL_USER_NAME}"/>
            <property name="jakarta.persistence.jdbc.password" value="${env.POSTGRESQL_USER_PASSWORD}"/>
            <property name="hibernate.show_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

Alternatively, we can define ENV properties separately like this
(in my case ENV variables was not loaded from `persistence.xml`, so I had to use such way):

```java
public class ArtSchoolFactory {

    public static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.user", System.getenv("POSTGRESQL_USER_NAME"));
        properties.put("jakarta.persistence.jdbc.password", System.getenv("POSTGRESQL_USER_PASSWORD"));
        return Persistence.createEntityManagerFactory("art_school", properties);
    }
} 
```

#### Persistence Unit: Java configuration

Another way is complete Java code:

- First add dependency for `Hikari` DataSource:

```kotlin
dependencies {
    //...
    implementation("com.zaxxer:HikariCP:6.1.0")
}
```

- Second implement `PersistenceUnitInfo` interface by completing next methods only
  and leave other methods returning `null`:

```java
public class ArtSchoolPersistenceUnitInfo implements PersistenceUnitInfo {

    @Override
    public String getPersistenceUnitName() {
        return "art_school";
    }

    @Override
    public String getPersistenceProviderClassName() {
        return HibernatePersistenceProvider.class.getName();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/hibernate");
        dataSource.setUsername(System.getenv("POSTGRESQL_USER_NAME"));
        dataSource.setPassword(System.getenv("POSTGRESQL_USER_PASSWORD"));
        return dataSource;
    }

    @Override
    public List<String> getManagedClassNames() {
        return List.of(ArtStudent.class.getName(),
                ArtTeacher.class.getName(),
                ArtClass.class.getName(),
                ArtReview.class.getName());
    }

    // leave other methods with no implementation
    @Override
    public SharedCacheMode getSharedCacheMode() {
        return null;
    }

    // ...
}
```

- And use it to create `EntityManagerFactory` like this:

```java
EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider()
        .createContainerEntityManagerFactory(new ArtSchoolPersistenceUnitInfo(), new HashMap());
```

### Hibernate practice

Now, we are ready to create DB tables and implement JPA layer in java.

#### Challenge: Define Schema in PostgreSQL

**Task**:

- Create schema `art_school` in `hibernate` database.
- Create table `art_teacher` with fields `id` as primary key, `name`.
- Create table `art_student` with fields `id` as primary key, `name`.
- Create table `art_class` with fields `id` as primary key, `name`, `week day`,
  and `teacher_id` as reference to `teacher` table.
- Create mapping for `art_student` and `art_class` tables as many-to-many relation.
- Create table `review` with fields `id`, `comment`, `rating`,
  and `teacher_id` as reference to `teacher` table.

**Solution example**:

```sql
CREATE SCHEMA IF NOT EXISTS art_school
    AUTHORIZATION postgres;
```

```sql
CREATE TABLE IF NOT EXISTS art_school.art_teachers(
    teacher_id   serial,
    teacher_name varchar(255),
    
    PRIMARY KEY (teacher_id)
);
```

Full list of commands is
[here .MD](sql/art_school_schema) or
[here .sql](sql/art_school_schema.sql)

Data structure looks like this in pgAdmin:

![](image/1.PNG)

#### Challenge: Define Entities in Java

**Task**:

- Create Java classes to map corresponding tables using `jakarta.persistence` annotations
- Implement standard Java methods using `lombok` annotations

**Solution example**:

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Entity
@Table(schema = "art_school", name = "art_teachers")
@NoArgsConstructor // Required: used by the JPA provider to create instances of the entity using reflection
@AllArgsConstructor // Optional: used by this app
@Setter // Optional: used by this app
@Getter // Optional: used by this app
public class ArtTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // when saving new Entity, ID is generated by DB
    @Column(name = "teacher_id")
    private int id;

    @Column(name = "teacher_name")
    private String name;
}
```

Full list of Entities is [here](Jpa-and-Hibernate/src/main/java/yevhent/demo/hibernate/entity).

#### Challenge: CRUD Operations in Persistent Context

**Task**:

Implement java methods for next operations

- Persist and Create new Student in DB using `EntityManager.persist()` method
- Merge and Create new Student in DB using `EntityManager.merge()` method
- Find existing Student in DB using `EntityManager.find()` method (without manual transaction management)
- Reference and Update existing Student in DB using `EntityManager.getReference()` and `ArtClass.setName()` methods
- Find and Update existing Student in DB using `EntityManager.find()` and `ArtClass.setName()` methods
- Merge and Update existing Student object using `EntityManager.merge()` method
- Reference and Delete existing Student by ID using `EntityManager.getReference()` and `EntityManager.remove()` methods
- Find and Delete existing Student by ID using `EntityManager.find()` and `EntityManager.remove()` methods

**Solution example**:

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class PersistAndCreateDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) { // session is opened once EntityManager is provided
            entityManager.getTransaction().begin(); // begin Transaction in order to follow ACID within this method
            // ID should be 0 for Entity creation.
            // Providing non-zero existing (or non-existing) ID will lead to EntityExistsException
            // Based on our Entity GenerationType = IDENTITY, Hibernate knows ID generation strategy, so creates insert statement with no ID.
            // Finally, ID is generated by PostgreSQL starting from 1.
            ArtStudent artStudent = new ArtStudent(0, "Persisted John");
            entityManager.persist(artStudent);
            // Hibernate prepares statement:
            // "Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id"
            // But changes remains in Hibernate context (in Java app)
            entityManager.getTransaction().commit(); // actual insert to DB
        } // session is closed by entityManager.close()
        // In case transaction is not committed, calling entityManager.close() also discards any staged changes in persistent context.
    }
}
```

The Student is saved to DB:

![](image/2.PNG)

Full list of operations is [here](Jpa-and-Hibernate/src/main/java/yevhent/demo/hibernate/context/crud).

#### Challenge: Entity Attachment in Persistent Context

**Task**:

Implement java methods for next operations

- Attach and Update existing Student in DB using `EntityManager.merge()` and `ArtClass.setName()` methods
- Find, Detach and Update existing Student in DB using `EntityManager.find()`, `EntityManager.detach()`
  and `ArtClass.setName()` methods
- Find, Update and Refresh existing Student in DB using `EntityManager.find()`, `ArtClass.setName()`
  and `EntityManager.refresh()` methods

**Solution example**:

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class AttachAndUpdateDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            // Consider we have exact same ArtStudent in DB:
            ArtStudent artStudent = new ArtStudent(1, "John");
            // ArtStudent Entity is out of Context
            artStudent = entityManager.merge(artStudent); // SELECT query to DB
            // Now Entity is synchronized and any further changes will be tracked in Context
            artStudent.setName("Updated John"); // new Name is in context
            entityManager.getTransaction().commit(); // UPDATE query to DB
        }
    }
}
```

Full list of operations is [here](Jpa-and-Hibernate/src/main/java/yevhent/demo/hibernate/context/attachment).

#### Challenge: Entity Relations in Persistent Context

**Given**:

Considered that we already have proper database schema implemented,
where tables have required relations using Foreign Key (FK),
like `FOREIGN KEY (theacher_id)` in `art_classes` table.
In DB, we need only one FK for each relation of table pair.
Regarding many-to-many relation, we should have additional Mapping table,
which is also covered by our DB schema above, it's `students_classes_mapping` table.
For the Java side, when implementing unidirectional relation,
we reflect the same state as in DB:
table that holds FK corresponds to Java Entity which "knows" about referenced Entity.
In case of bidirectional relation, we additionally add back reference to original Entity.
We can use bidirectional relation to access underlying Entities from both sides or
when there is no FK in table that we have in the first place.
Also, we don't need Java Entity for Mapping table.

**Task**:

Update java entity classes for next relations:

- Implement unidirectional one-to-one relationship between `ArtClass` and `ArtTeacher` using `@OneToOne`
  and `@JoinColumn`.
  Make `ArtClass` refer to `ArtTeacher`.
- Implement bidirectional one-to-many relationship between `ArtTeacher` and `ArtReview` using `@OneToMany`, `@ManyToOne`
  and `@JoinColumn`.
- Implement unidirectional many-to-many relationship between `ArtClass` and `ArtStudent`
  using `@ManyToMany`, `@JoinTable` and `@JoinColumn`.
  Make `ArtStudent` refer to `ArtClass`.

Implement java demo methods for next cases:

- Create and save a new pair of `ArtClass` and `ArtTeacher` to DB.
- Read `ArtTeacher` from DB and get `ArtClass` as its property.
- Create and save new `ArtTeacher` with set of related `ArtReview`s to DB.
- Read `ArtTeacher` from DB and get list of `ArtReview`s as its property.
- Read any `ArtReview` from DB and get `ArtTeacher` as its property.
- Create and save new sets of `ArtClass`s and `ArtStudent`s to DB.
- Read any `ArtClass` from DB and get list of `ArtStudent`s as its property.

**Solution example**:

- Changes for Java entity:

```java
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Table(schema = "art_school", name = "art_classes")
// ...
public class ArtClass {
    // ...
    @OneToOne
    @JoinColumn(name = "teacher_id") // Reflects FOREIGN KEY (teacher_id) REFERENCES art_teachers(teacher_id)
    private ArtTeacher artTeacher;
}
```

- Insert demo:

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtClass;
import yevhent.demo.hibernate.entity.ArtTeacher;

/**
 * Unidirectional relation between Teacher and Class, 
 * where `art_classes` table has FK as reference to `art_teachers` 
 * and ArtClass object contains ArtTeacher object
 */
public class InsertOneToOneDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            ArtTeacher artTeacher = new ArtTeacher(0, "John");
            ArtClass artClass = new ArtClass(0, "Painting", "Monday", artTeacher);
            entityManager.persist(artTeacher);
            // Hibernate: insert into art_school.art_teachers (teacher_name) values (?) returning teacher_id
            entityManager.persist(artClass);
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            entityManager.getTransaction().commit();
            // insert ArtClass to DB
            // insert ArtTeacher to DB
        }
    }
}
```

Full list of relations is [here](Jpa-and-Hibernate/src/main/java/yevhent/demo/hibernate/context/relation).

#### Challenge: CRUD Operations with Queries

**Task**:

Using `JPQL`, `Named`, `Native` and `Criteria` queries, implement Java methods for next operations:

- Create 5 Teachers: `Mr. Smith`, `Ms. Johnson`, `Dr. Miller`, `Prof. Davis`, `Mrs. Williams`
- Create from 3 to 10 Reviews done by each Teacher
- Find Teachers which have Reviews more than given number
- Find average rating of Reviews for each Teacher,
  return greater than given average value, arrange in the descending order
- Round rating to the nearest tenth of Reviews for given Teacher
- Delete Reviews of given Teacher with rating lower than given value

**Solution example**:

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;
import yevhent.demo.hibernate.query.crud.CrudQuery;
import yevhent.demo.hibernate.query.crud.CrudQueryUser;

public class JpqlQueryDemo implements CrudQuery {

    @Override
    public Map<String, Long> findTeachersWithReviewNumberMore(int teacherIdFrom, int teacherIdTo, int minNumberOfReviews) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String jpql = """
                    SELECT r.artTeacher.id AS id, r.artTeacher.name AS name, COUNT(r) AS number
                    FROM ArtReview r
                    WHERE r.artTeacher.id BETWEEN :teacherIdFrom AND :teacherIdTo
                    GROUP BY r.artTeacher.id, r.artTeacher.name
                    HAVING COUNT(r) > :minNumber
                    """;
            TypedQuery<Tuple> query = entityManager.createQuery(jpql, Tuple.class);
            query.setParameter("teacherIdFrom", teacherIdFrom);
            query.setParameter("teacherIdTo", teacherIdTo);
            query.setParameter("minNumber", minNumberOfReviews);
            List<Tuple> rows = query.getResultList();
            // Hibernate: select ar1_0.teacher_id,at1_0.teacher_name,count(ar1_0.review_id)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            where ar1_0.teacher_id between ? and ?
            //            group by ar1_0.teacher_id,at1_0.teacher_name
            //            having count(ar1_0.review_id)>?
            Map<String, Long> teacherReviewNumbers = rows.stream()
                    .peek(row -> System.out.printf("Found Teacher(%d, \"%s\") with %s reviews.\n",
                            row.get("id", Integer.class), row.get("name", String.class), row.get("number", Long.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Teacher(%d, \"%s\")", row.get("id", Integer.class), row.get("name", String.class)),
                            row -> row.get("number", Long.class)));
            return teacherReviewNumbers;
        }
    }
}
```

Complete demos are [here](Jpa-and-Hibernate/src/main/java/yevhent/demo/hibernate/query).

#### Challenge: Service vs Repository

Now we will check How data access and transaction management
are implemented according to layered architecture best practices.

**Task**:

Implement layered architecture for data access and processing:

- Declare `CrudRepository` interface for CRUD methods declaration
- Implement `CrudRepository` interface by `BaseCrudRepository` class.
- Extend `CrudRepository` interface by `ArtTeacherRepository` interface with more specific find operation.
- Implement `ArtTeacherRepository` interface by `ArtTeacherRepositoryImpl` class extending `BaseCrudRepository` class.
- Create `ArtTeacherService` class to use `ArtTeacherRepositoryImpl` class.
- Make sure data access happens at Repository layer
- Make sure transactions are managed at Service layer
- Create demo class for using Service and underlying Repository.

**Solution example**:

```java
import jakarta.persistence.EntityManager;
import yevhent.demo.hibernate.entity.ArtTeacher;

public class ArtTeacherRepositoryImpl extends BaseCrudRepository<ArtTeacher> implements ArtTeacherRepository {

    @Override
    public List<ArtTeacher> findEagerTeachersByIds(EntityManager entityManager, List<Integer> teacherIds) {
        return entityManager.createQuery("""
                        SELECT t FROM ArtTeacher t
                        LEFT JOIN FETCH t.artReviews
                        WHERE t.id IN :teacherIds
                        """, ArtTeacher.class)
                .setParameter("teacherIds", teacherIds)
                .getResultList();
    }
}
```

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;

@RequiredArgsConstructor
public class ArtSchoolService {

    private final ArtTeacherRepository teacherRepository;
    private final EntityManagerFactory entityManagerFactory;

    public List<Integer> saveTeachersWithReviews(Map<String, List<Integer>> teacherRequest) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            List<ArtTeacher> persistedTeachers = teacherRequest.keySet().stream()
                    .map(name -> teacherRepository.create(entityManager, new ArtTeacher(name))).toList();
            entityManager.flush();

            persistedTeachers.forEach(teacher -> {
                List<Integer> ratings = teacherRequest.get(teacher.getName());
                List<ArtReview> reviews = ratings.stream()
                        .map(rating -> new ArtReview(getComment(teacher, rating), rating, teacher)).toList();
                teacher.setArtReviews(reviews);
            });
            entityManager.getTransaction().commit();
            return persistedTeachers.stream().map(ArtTeacher::getId).toList();
        }
    }
}
```

Complete implementation is [here](Jpa-and-Hibernate/src/main/java/yevhent/demo/hibernate/repository).

#### Challenge: JPA, Hibernate and PSQL Exceptions

**Task**:

Implement java methods that leads to next exceptions
with corresponding stack including JPA, Hibernate and PSQL exceptions (if any):

**JPA** `jakarta.persistence.PersistenceException` exceptions:
   1. EntityNotFound `jakarta.persistence.EntityNotFoundException`
   2. NoResult `jakarta.persistence.NoResultException`
   3. TransactionRequired `jakarta.persistence.TransactionRequiredException`

**Hibernate** `org.hibernate.HibernateException` exceptions:
   1. LazyInitialization 
      - `org.hibernate.LazyInitializationException`
   2. NonUniqueObject 
      - `jakarta.persistence.EntityExistsException`
        - `org.hibernate.NonUniqueObjectException`
   3. NonUniqueResult
      - `jakarta.persistence.NonUniqueResultException`
        - `org.hibernate.NonUniqueResultException`
   4. StaleState
      - `jakarta.persistence.RollbackException`
        - `jakarta.persistence.OptimisticLockException`
          - `org.hibernate.StaleStateException`

**PSQL** `org.postgresql.util.PSQLException` exceptions:
   1. ConstraintViolation 
      - `jakarta.persistence.RollbackException`
        -  `org.hibernate.exception.ConstraintViolationException`
           - `org.postgresql.util.PSQLException`
   2. LockTimeout 
      - `jakarta.persistence.LockTimeoutException`
        - `org.hibernate.PessimisticLockException`
           - `org.postgresql.util.PSQLException`
   3. PessimisticLock 
      - `jakarta.persistence.PessimisticLockException`
        - `org.hibernate.PessimisticLockException`
           - `org.postgresql.util.PSQLException`
   4. QueryTimeoutException 
      - `jakarta.persistence.QueryTimeoutException`
          - `org.hibernate.QueryTimeoutException`
              - `org.postgresql.util.PSQLException`


- `org.hibernate.ObjectNotFoundException`

- `org.hibernate.TransactionException`