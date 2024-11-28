package yevhent.demo.hibernate.context.relation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtClass;
import yevhent.demo.hibernate.entity.ArtStudent;

import java.util.List;

/**
 * Unidirectional relation between Student and Class,
 * where `art_students` and `art_classes` tables are related by `students_classes_mapping` table
 * and ArtStudent object contains list of ArtClasses objects
 */
public class SelectManyToManyDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            ArtStudent artStudent;
            List<ArtClass> artClasses;

            // ArtStudents with ID = 1,2,3,4,5,6 and related ArtClasses must be persisted in DB before running SelectManyToManyDemo
            artStudent = entityManager.find(ArtStudent.class, 1);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artClasses = artStudent.getArtClasses();
            // Hibernate: select ac1_0.student_id,ac1_1.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_1.class_name,ac1_1.week_day
            //            from art_school.students_classes_mapping ac1_0 join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_1.teacher_id where ac1_0.student_id=?
            println(artStudent, artClasses);
            // Student John attends Painting and Music classes.

            artStudent = entityManager.find(ArtStudent.class, 2);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artClasses = artStudent.getArtClasses();
            // Hibernate: select ac1_0.student_id,ac1_1.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_1.class_name,ac1_1.week_day
            //            from art_school.students_classes_mapping ac1_0 join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_1.teacher_id where ac1_0.student_id=?
            println(artStudent, artClasses);
            // Student Alice attends Sculpture and Design classes.

            artStudent = entityManager.find(ArtStudent.class, 3);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artClasses = artStudent.getArtClasses();
            // Hibernate: select ac1_0.student_id,ac1_1.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_1.class_name,ac1_1.week_day
            //            from art_school.students_classes_mapping ac1_0 join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_1.teacher_id where ac1_0.student_id=?
            println(artStudent, artClasses);
            // Student Bob attends Photography and Theatre classes.

            artStudent = entityManager.find(ArtStudent.class, 4);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artClasses = artStudent.getArtClasses();
            // Hibernate: select ac1_0.student_id,ac1_1.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_1.class_name,ac1_1.week_day
            //            from art_school.students_classes_mapping ac1_0 join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_1.teacher_id where ac1_0.student_id=?
            println(artStudent, artClasses);
            // Student Charlie attends Design and Dance classes.

            artStudent = entityManager.find(ArtStudent.class, 5);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artClasses = artStudent.getArtClasses();
            // Hibernate: select ac1_0.student_id,ac1_1.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_1.class_name,ac1_1.week_day
            //            from art_school.students_classes_mapping ac1_0 join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_1.teacher_id where ac1_0.student_id=?
            println(artStudent, artClasses);
            // Student Diana attends Design and Photography classes.

            artStudent = entityManager.find(ArtStudent.class, 6);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artClasses = artStudent.getArtClasses();
            // Hibernate: select ac1_0.student_id,ac1_1.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_1.class_name,ac1_1.week_day
            //            from art_school.students_classes_mapping ac1_0 join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_1.teacher_id where ac1_0.student_id=?
            println(artStudent, artClasses);
            // Student Eve attends Sculpture and Theatre classes.

            artStudent = entityManager.find(ArtStudent.class, 7);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artClasses = artStudent.getArtClasses();
            // Hibernate: select ac1_0.student_id,ac1_1.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_1.class_name,ac1_1.week_day
            //            from art_school.students_classes_mapping ac1_0 join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_1.teacher_id where ac1_0.student_id=?
            println(artStudent, artClasses);
            // Student Frank attends Music and Dance classes.
        }
    }

    static void println(ArtStudent artStudent, List<ArtClass> artClasses) {
        System.out.printf("Student %s attends %s and %s classes.\n",
                artStudent.getName(),
                artClasses.get(0).getName(),
                artClasses.get(1).getName());
    }
}