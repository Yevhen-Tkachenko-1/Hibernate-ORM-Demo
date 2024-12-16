PostgreSQL Schema:
```sql
CREATE SCHEMA IF NOT EXISTS art_school
    AUTHORIZATION postgres;
```

**ArtTeacher** Table:
```sql
CREATE TABLE IF NOT EXISTS art_school.art_teachers(
    teacher_id   serial,
    teacher_name varchar(255),
    
    PRIMARY KEY (teacher_id)
);
```

**ArtStudent** Table:
```sql
CREATE TABLE IF NOT EXISTS art_school.art_students(
    student_id   serial,
    student_name varchar(255),
    
    PRIMARY KEY (student_id)
);
```

**ArtClass** Table:
```sql
CREATE TABLE IF NOT EXISTS art_school.art_classes(
    class_id    serial,
    class_name  varchar(255),
    week_day    varchar(255),
    teacher_id  int,

    PRIMARY KEY (class_id),
    FOREIGN KEY (teacher_id) REFERENCES art_school.art_teachers(teacher_id) 
        MATCH SIMPLE 
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);
```

**ArtClasses** to **ArtStudents** relation Table:
```sql
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

**ArtReview** Table
```sql
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