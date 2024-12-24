package yevhent.demo.hibernate.context.relation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtClass;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;

/**
 * Unidirectional relation between Teacher and Class,
 * where `art_classes` table has FK as reference to `art_teachers`
 * and ArtClass object contains ArtTeacher object
 */
public class InsertOneToOneDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            ArtTeacher artTeacher = new ArtTeacher(0, "John");
            ArtClass artClass = new ArtClass(0, "Painting", "Monday");
            artClass.setArtTeacher(artTeacher);
            entityManager.persist(artTeacher);
            // Hibernate: insert into art_school.art_teachers (teacher_name) values (?) returning teacher_id
            entityManager.persist(artClass);
            // Hibernate: insert into art_school.art_classes (teacher_id,class_name,week_day) values (?,?,?) returning class_id
            entityManager.getTransaction().commit();
            // insert ArtClass to DB
            // insert ArtTeacher to DB
        }
    }
}