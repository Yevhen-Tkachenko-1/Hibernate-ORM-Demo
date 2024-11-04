package yevhent.demo.springboot;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa_and_hibernate")) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
        }
    }
}