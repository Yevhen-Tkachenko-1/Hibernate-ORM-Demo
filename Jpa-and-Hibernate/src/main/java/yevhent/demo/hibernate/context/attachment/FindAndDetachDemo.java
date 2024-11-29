package yevhent.demo.hibernate.context.attachment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class FindAndDetachDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            // ArtStudent with ID = 1 must be persisted in DB before running FindAndDetachDemo
            ArtStudent artStudent = entityManager.find(ArtStudent.class, 1); // SELECT query to DB
            // ArtStudent Entity is in Context
            entityManager.detach(artStudent);
            // ArtStudent Entity is out of Context
            artStudent.setName("Not Updated John"); // Name "Not Updated John" is not tracked
            entityManager.getTransaction().commit(); // NO query to DB
            // Name "Not Updated John" is not saved
        }
    }
}