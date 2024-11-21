package yevhent.demo.hibernate.jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtStudent;

import java.util.List;

public class JpqlQueryDemo {

    public static void main(String[] args) {

        System.out.println("All ArtStudents: " + findAllStudents());
        // All ArtStudents: [ArtStudent(id=1, name=John), ArtStudent(id=2, name=Alice), ArtStudent(id=3, name=Bob), ArtStudent(id=4, name=Charlie), ArtStudent(id=5, name=Diana), ArtStudent(id=6, name=Eve), ArtStudent(id=7, name=Frank)]
    }

    public static List<ArtStudent> findAllStudents() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT s FROM ArtStudent s";
            TypedQuery<ArtStudent> q1 = entityManager.createQuery(s1, ArtStudent.class);
            List<ArtStudent> artStudents = q1.getResultList();
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0
            // Select query to DB
            return artStudents;
        }
    }
}
