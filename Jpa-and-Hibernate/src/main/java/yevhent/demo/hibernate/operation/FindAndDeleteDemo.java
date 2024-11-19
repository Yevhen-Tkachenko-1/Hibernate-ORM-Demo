package yevhent.demo.hibernate.operation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class FindAndDeleteDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            // ArtStudent with ID = 1 must be persisted in DB before running FindAndDeleteDemo
            ArtStudent artStudent = entityManager.find(ArtStudent.class, 1);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            // call to DB
            entityManager.remove(artStudent);
            // Hibernate keeps changes in persistent context by moving it to "removed" state
            entityManager.getTransaction().commit();
            // Hibernate: delete from art_school.art_students where student_id=?
            // call to DB
        }
    }
}