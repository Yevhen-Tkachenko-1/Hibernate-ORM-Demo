package yevhent.demo.hibernate.exception;

import jakarta.persistence.*;
import org.hibernate.LazyInitializationException;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;
import yevhent.demo.hibernate.entity.SelfIdentifiable;
import yevhent.demo.hibernate.entity.VersionedItem;

public class HibernateExceptionsDemo {

    public static void main(String[] args) {
        ExceptionUtil.hideLowLevelLogs();
        ExceptionUtil.setupUncaughtExceptionHandler();

        System.out.println("=====throwLazyInitializationException============================================");
        throwLazyInitializationException();
        System.out.println("=====throwNonUniqueObjectException============================================");
        throwNonUniqueObjectException();
        System.out.println("=====throwNonUniqueResultException====================================================");
        throwNonUniqueResultException();
        System.out.println("=====throwStaleStateException====================================================");
        throwStaleStateException();
        System.out.println("=====END====================================================");
    }

    static void throwLazyInitializationException() {
        int teacherId = ExceptionUtil.saveNewTeacherAndReviewsToDB();
        ArtTeacher teacher;
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            teacher = entityManager.find(ArtTeacher.class, teacherId);
        } // EntityManager is closed by try-with-resources (reason of LazyInitializationException)
        try {
            teacher.getArtReviews().size(); // throws Exception
        } catch (LazyInitializationException e) {
            System.out.println(e);
            // org.hibernate.LazyInitializationException:
            // failed to lazily initialize a collection of role:
            // yevhent.demo.hibernate.entity.ArtTeacher.artReviews:
            // could not initialize proxy - no Session
        }
    }

    static void throwNonUniqueObjectException() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(new SelfIdentifiable(1));
            entityManager.persist(new SelfIdentifiable(1)); // throws Exception
            entityManager.getTransaction().commit();
        } catch (EntityExistsException e) {
            System.out.println(e);
            // jakarta.persistence.EntityExistsException:
            // A different object with the same identifier value was already associated with the session
            System.out.println(e.getCause());
            // org.hibernate.NonUniqueObjectException:
            // A different object with the same identifier value was already associated with the session
        }
    }

    static void throwNonUniqueResultException() {
        int teacherId = ExceptionUtil.saveNewTeacherAndReviewsToDB();
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Query that returns multiple rows
            TypedQuery<ArtReview> query = entityManager.createQuery(
                    "SELECT r FROM ArtReview r WHERE r.artTeacher.id = :teacherId", ArtReview.class);
            query.setParameter("teacherId", teacherId);
            // Expecting a single result, but ArtTeacher has multiple ArtReviews
            ArtReview artReview = query.getSingleResult(); // Throws Exception
        } catch (NonUniqueResultException e) {
            System.out.println(e);
            // jakarta.persistence.NonUniqueResultException:
            // Query did not return a unique result: 5 results were returned
            System.out.println(e.getCause());
            // org.hibernate.NonUniqueResultException:
            // Query did not return a unique result: 5 results were returned
        }
    }

    static void throwStaleStateException() {
        int itemId = ExceptionUtil.saveNewVersionedItemToDB();
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager1 = entityManagerFactory.createEntityManager();
             EntityManager entityManager2 = entityManagerFactory.createEntityManager()) {

            entityManager1.getTransaction().begin();
            VersionedItem item11 = entityManager1.find(VersionedItem.class, itemId);

            entityManager2.getTransaction().begin();
            VersionedItem item12 = entityManager2.find(VersionedItem.class, itemId);
            item12.setName("Item2222(set by Transaction2)");
            entityManager2.getTransaction().commit();

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
}