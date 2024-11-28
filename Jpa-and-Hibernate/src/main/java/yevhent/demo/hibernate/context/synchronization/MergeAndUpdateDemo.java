package yevhent.demo.hibernate.context.synchronization;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class MergeAndUpdateDemo {
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