package yevhent.demo.hibernate.context.attachment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class FindAndRefreshDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            // ArtStudent with ID = 1 must be persisted in DB before running FindAndDetachDemo
            ArtStudent artStudent = entityManager.find(ArtStudent.class, 5); // SELECT query to DB
            // ArtStudent Entity is in Context
            artStudent.setName("Not Updated John"); // new Name is in context
            entityManager.refresh(artStudent); // SELECT query to DB
            // Name is rolled back to DB Name
            entityManager.getTransaction().commit(); // NO query to DB
            // Name "Not Updated John" is not saved
        }
    }
}