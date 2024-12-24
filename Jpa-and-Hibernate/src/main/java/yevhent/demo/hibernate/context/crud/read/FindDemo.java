package yevhent.demo.hibernate.context.crud.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtStudent;

public class FindDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // We don't need to entityManager.getTransaction().begin(); as operations are read only
            // ArtStudent with ID = 1 must be persisted in DB before running FindAndUpdateDemo
            ArtStudent artStudent = entityManager.find(ArtStudent.class, 1);
            // JPA automatically performs something like entityManager.getTransaction().begin();
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            // call to DB
            System.out.println("Student found: " + artStudent.getName());
            // We don't need to entityManager.getTransaction().commit();
        }
    }
}