package yevhent.demo.hibernate.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;
import org.hibernate.LazyInitializationException;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtTeacher;
import yevhent.demo.hibernate.entity.SelfIdentifiable;
import yevhent.demo.hibernate.entity.VersionedItem;

public class HibernateExceptionsDemo {

    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("===== Uncaught exception in thread \"" + t.getName() + "\":");
            System.out.println(e);
        });
        System.out.println("=====throwNonUniqueObjectException============================================");
        throwNonUniqueObjectException();
        System.out.println("=====throwLazyInitializationException============================================");
        throwLazyInitializationException();
        System.out.println("=====throwStaleStateException====================================================");
        throwStaleStateException();
        System.out.println("=====throwConstraintViolationException====================================================");
        //throwConstraintViolationException();
        System.out.println("=====END====================================================");
    }

    static void throwNonUniqueObjectException() {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            entityManager.persist(new SelfIdentifiable(1));
            entityManager.persist(new SelfIdentifiable(1)); // throws EntityExistsException
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

    static void throwLazyInitializationException() {

        int teacherId = ExceptionRepository.saveNewTeacherAndReviewsToDB();
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

        int itemId = ExceptionRepository.saveNewVersionedItemToDB();

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

    static void throwConstraintViolationException() {

        int id = 2;
        ExceptionRepository.saveSelfIdentifiableToDBIfNotPresent(id);

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            entityManager.persist(new SelfIdentifiable(id)); // throws RollbackException
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            System.out.println(e);
            System.out.println(e.getCause());
            System.out.println(e.getCause().getCause());
        }
    }
}