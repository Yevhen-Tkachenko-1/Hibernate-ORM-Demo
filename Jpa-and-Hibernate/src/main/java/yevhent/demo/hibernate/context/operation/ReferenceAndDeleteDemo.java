package yevhent.demo.hibernate.context.operation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class ReferenceAndDeleteDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            // ArtStudent with ID = 1 must be persisted in DB before running ReferenceAndDeleteDemo
            ArtStudent artStudent = entityManager.getReference(ArtStudent.class, 1);
            // ArtStudent is just proxy object (with ID field only) implementing lazy loading
            entityManager.remove(artStudent);
            // Hibernate does not need to initialize the proxy or fetch additional data
            entityManager.getTransaction().commit();
            // Hibernate: delete from art_school.art_students where student_id=?
            // call to DB
        }
    }
}