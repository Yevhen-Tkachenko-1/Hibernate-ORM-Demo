package yevhent.demo.hibernate.operation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

public class FindAndUpdateDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            ArtStudent artStudent = entityManager.find(ArtStudent.class, 1);
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0 where as1_0.student_id=?
            artStudent.setName("James");
            entityManager.getTransaction().commit();
            // Hibernate: update art_school.art_students set student_name=? where student_id=?
        }
    }
}