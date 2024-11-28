package yevhent.demo.hibernate.context.relation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtClass;

/**
 * Unidirectional relation between Teacher and Class,
 * where `art_classes` table has FK as reference to `art_teachers`
 * and ArtClass object contains ArtTeacher object
 */
public class SelectOneToOneDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            // ArtClass with ID = 1 and referenced ArtTeacher must be persisted in DB before running SelectOneToOneDemo
            ArtClass artClass = entityManager.find(ArtClass.class, 1);
            // Hibernate: select ac1_0.class_id,at1_0.teacher_id,at1_0.teacher_name,ac1_0.class_name,ac1_0.week_day
            //            from art_school.art_classes ac1_0
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ac1_0.teacher_id
            //            where ac1_0.class_id=?
            String className = artClass.getName();
            String classWeekDay = artClass.getWeekDay();
            String teacherName = artClass.getArtTeacher().getName();
            System.out.printf("Class %s is scheduled on %s and has Teacher %s.%n",
                    className, classWeekDay, teacherName);
            // Log output: "Class Painting is scheduled on Monday and has Teacher John."
        }
    }
}