package yevhent.demo.hibernate.exception;

import jakarta.persistence.*;
import org.hibernate.LazyInitializationException;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;
import yevhent.demo.hibernate.entity.SelfIdentifiable;
import yevhent.demo.hibernate.entity.VersionedItem;

import java.util.List;
import java.util.stream.IntStream;

public class JpaExceptionsDemo {

    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("===== Uncaught exception in thread \"" + t.getName() + "\":");
            System.out.println(e);
        });
        System.out.println("=====throwTransactionRequiredException===========================================");
        throwTransactionRequiredException();
        System.out.println("=====throwEntityExistsException============================================");
        throwEntityExistsException();
        System.out.println("=====throwLazyInitializationException============================================");
        throwLazyInitializationException();
        System.out.println("=====throwStaleStateException====================================================");
        throwStaleStateException();
        System.out.println("=====END====================================================");
    }

    static void throwTransactionRequiredException() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            ArtTeacher teacher = new ArtTeacher("John Doe");
            entityManager.persist(teacher);
            entityManager.flush(); // throws TransactionRequiredException
        } catch (TransactionRequiredException e) {
            System.out.println(e);
            // jakarta.persistence.TransactionRequiredException: no transaction is in progress
        }
    }

    static void throwEntityExistsException() {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            entityManager.persist(new SelfIdentifiable(1));
            entityManager.persist(new SelfIdentifiable(1)); // throws EntityExistsException

        } catch (EntityExistsException e) {
            System.out.println(e);
            // jakarta.persistence.EntityExistsException:
            // A different object with the same identifier value was already associated with the session
            System.out.println(e.getCause());
            // org.hibernate.NonUniqueObjectException:
            // A different object with the same identifier value was already associated with the session
        }
    }

    static void throwLazyInitializationException() {

        int teacherId = createNewTeacherInDB();
        ArtTeacher teacher;

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            teacher = entityManager.find(ArtTeacher.class, teacherId);
        } // EntityManager is closed by try-with-resources (reason of LazyInitializationException)
        try {
            teacher.getArtReviews().size(); // throws LazyInitializationException
        } catch (LazyInitializationException e) {
            System.out.println(e);
            // org.hibernate.LazyInitializationException:
            // failed to lazily initialize a collection of role:
            // yevhent.demo.hibernate.entity.ArtTeacher.artReviews:
            // could not initialize proxy - no Session
        }
    }

    static void throwStaleStateException() {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager1 = entityManagerFactory.createEntityManager();
             EntityManager entityManager2 = entityManagerFactory.createEntityManager()) {

            entityManager1.getTransaction().begin();

            VersionedItem item = new VersionedItem("Item0000");
            entityManager1.persist(item);
            entityManager1.getTransaction().commit();

            VersionedItem item11 = entityManager1.find(VersionedItem.class, item.getId());

            entityManager2.getTransaction().begin();
            VersionedItem item12 = entityManager2.find(VersionedItem.class, item.getId());
            item12.setName("Item2222(set by Transaction2)");
            entityManager2.getTransaction().commit();

            entityManager1.getTransaction().begin();
            item11.setName("Item1111(set by Transaction1)");
            entityManager1.getTransaction().commit(); // Throws RollbackException
        } catch (RollbackException e) {
            System.out.println(e);
            // jakarta.persistence.RollbackException:
            // Error while committing the transaction
            System.out.println(e.getCause());
            // jakarta.persistence.OptimisticLockException:
            // Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect):
            // [yevhent.demo.hibernate.entity.VersionedItem#33]
            System.out.println(e.getCause().getCause());
            // org.hibernate.StaleObjectStateException:
            // Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect):
            // [yevhent.demo.hibernate.entity.VersionedItem#33]
        }
    }


    static int createNewTeacherInDB() {
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
}