package yevhent.demo.hibernate.context.relation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtClass;
import yevhent.demo.hibernate.entity.ArtStudent;

import java.util.ArrayList;
import java.util.List;

/**
 * Unidirectional relation between Student and Class,
 * where `art_students` and `art_classes` tables are related by `students_classes_mapping` table
 * and ArtStudent object contains list of ArtClasses objects
 */
public class InsertManyToManyDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            List<ArtClass> artClasses = new ArrayList();
            artClasses.add(new ArtClass(0, "Painting", "Monday"));
            artClasses.add(new ArtClass(0, "Sculpture", "Tuesday"));
            artClasses.add(new ArtClass(0, "Design", "Wednesday"));
            artClasses.add(new ArtClass(0, "Photography", "Thursday"));
            artClasses.add(new ArtClass(0, "Theatre", "Friday"));
            artClasses.add(new ArtClass(0, "Music", "Saturday"));
            artClasses.add(new ArtClass(0, "Dance", "Sunday"));

            List<ArtStudent> artStudents = new ArrayList();
            artStudents.add(new ArtStudent(0, "John", List.of(artClasses.get(0), artClasses.get(5))));
            artStudents.add(new ArtStudent(0, "Alice", List.of(artClasses.get(1), artClasses.get(2))));
            artStudents.add(new ArtStudent(0, "Bob", List.of(artClasses.get(3), artClasses.get(4))));
            artStudents.add(new ArtStudent(0, "Charlie", List.of(artClasses.get(2), artClasses.get(6))));
            artStudents.add(new ArtStudent(0, "Diana", List.of(artClasses.get(2), artClasses.get(3))));
            artStudents.add(new ArtStudent(0, "Eve", List.of(artClasses.get(1), artClasses.get(4))));
            artStudents.add(new ArtStudent(0, "Frank", List.of(artClasses.get(5), artClasses.get(6))));

            entityManager.persist(artStudents.get(0));
            // Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            entityManager.persist(artStudents.get(1));
            // Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            entityManager.persist(artStudents.get(2));
            // Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            entityManager.persist(artStudents.get(3));
            // Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            // Second ArtClass already persisted
            entityManager.persist(artStudents.get(4));
            // Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id
            // First ArtClass already persisted
            // Second ArtClass already persisted
            entityManager.persist(artStudents.get(5));
            // Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id
            // First ArtClass already persisted
            // Second ArtClass already persisted
            entityManager.persist(artStudents.get(6));
            // Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id
            // First ArtClass already persisted
            // Second ArtClass already persisted
            entityManager.getTransaction().commit();
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)
            //Hibernate: insert into art_school.students_classes_mapping (student_id,class_id) values (?,?)

            // Insert ArtStudent[0] to DB
            // Insert ArtStudent[1] to DB
            // Insert ArtStudent[2] to DB
            // Insert ArtStudent[3] to DB
            // Insert ArtStudent[4] to DB
            // Insert ArtStudent[5] to DB
            // Insert ArtStudent[6] to DB
            // Insert ArtClass[0] to DB
            // Insert ArtClass[1] to DB
            // Insert ArtClass[2] to DB
            // Insert ArtClass[3] to DB
            // Insert ArtClass[4] to DB
            // Insert ArtClass[5] to DB
            // Insert ArtClass[6] to DB
            // Insert ArtStudent-ArtClass-Relation[0] to DB
            // Insert ArtStudent-ArtClass-Relation[1] to DB
            // Insert ArtStudent-ArtClass-Relation[2] to DB
            // Insert ArtStudent-ArtClass-Relation[3] to DB
            // Insert ArtStudent-ArtClass-Relation[4] to DB
            // Insert ArtStudent-ArtClass-Relation[5] to DB
            // Insert ArtStudent-ArtClass-Relation[6] to DB
            // Insert ArtStudent-ArtClass-Relation[7] to DB
            // Insert ArtStudent-ArtClass-Relation[8] to DB
            // Insert ArtStudent-ArtClass-Relation[9] to DB
            // Insert ArtStudent-ArtClass-Relation[10] to DB
            // Insert ArtStudent-ArtClass-Relation[11] to DB
        }
    }
}