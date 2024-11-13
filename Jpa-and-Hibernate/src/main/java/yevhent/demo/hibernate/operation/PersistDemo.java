package yevhent.demo.hibernate.operation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class PersistDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) { // session is opened here once EntityManager is provided
            entityManager.getTransaction().begin(); // begin Transaction in order to follow ACID within this method

            ArtStudent artStudent = new ArtStudent(0, "John5"); // id = 0 is just default value, will be overridden later
            entityManager.persist(artStudent); // logging: 'Hibernate: insert into art_school.art_students (student_name) values (?) returning student_id'
            // changes are in Hibernate context (in Java app)
            entityManager.getTransaction().commit(); // actual insert to DB happens here
        } // session is closed here by entityManager.close()
    }
}