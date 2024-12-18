package yevhent.demo.hibernate.exception;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;
import yevhent.demo.hibernate.entity.SelfIdentifiable;
import yevhent.demo.hibernate.entity.VersionedItem;

import java.util.List;
import java.util.stream.IntStream;

public class ExceptionRepository {

    static int saveNewTeacherAndReviewsToDB() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            ArtTeacher teacher = new ArtTeacher("Prof. Davis");
            List<ArtReview> artReviews = IntStream.range(0, 5).boxed().map(i -> new ArtReview("Review number " + i, i * 10, teacher)).toList();
            teacher.setArtReviews(artReviews);
            entityManager.persist(teacher);
            entityManager.getTransaction().commit();
            return teacher.getId();
        }
    }

    static void deleteTeacherAndReviewsFromDB(int teacherId) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            ArtTeacher artTeacher = entityManager.find(ArtTeacher.class, teacherId);
            artTeacher.getArtReviews().forEach(review -> entityManager.remove(review));
            artTeacher.getArtReviews().clear();
            entityManager.remove(artTeacher);
            entityManager.getTransaction().commit();
        }
    }

    static int saveNewVersionedItemToDB() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            VersionedItem item = new VersionedItem("Item0000");
            entityManager.persist(item);
            entityManager.getTransaction().commit();
            return item.getId();
        }
    }

    static void saveSelfIdentifiableToDBIfNotPresent(int id) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            entityManager.merge(new SelfIdentifiable(id));
            entityManager.getTransaction().commit();
        }
    }
}