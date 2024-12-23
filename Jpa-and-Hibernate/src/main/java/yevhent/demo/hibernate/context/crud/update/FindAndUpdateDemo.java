package yevhent.demo.hibernate.context.crud.update;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtStudent;

public class FindAndUpdateDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            // ArtStudent with ID = 1 must be persisted in DB before running FindAndUpdateDemo
            ArtStudent artStudent = entityManager.find(ArtStudent.class, 1);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            // call to DB
            artStudent.setName("Found and Updated John");
            // Entity is already in Hibernate context, so no statements printed and Hibernate just does dirty checking
            entityManager.getTransaction().commit();
            // Hibernate: update art_school.art_students set student_name=? where student_id=?
            // call to DB
        }
    }
}