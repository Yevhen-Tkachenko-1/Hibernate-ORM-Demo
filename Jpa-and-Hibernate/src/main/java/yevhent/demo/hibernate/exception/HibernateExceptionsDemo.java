package yevhent.demo.hibernate.exception;

import jakarta.persistence.*;
import org.hibernate.LazyInitializationException;
import org.hibernate.PropertyValueException;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtReview;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;
import yevhent.demo.hibernate.entity.general.MandatoryNamedItem;
import yevhent.demo.hibernate.entity.general.SelfIdentifiable;
import yevhent.demo.hibernate.entity.general.UnknownEntity;
import yevhent.demo.hibernate.entity.general.VersionedItem;

public class HibernateExceptionsDemo {

    public static void main(String[] args) {
        ExceptionUtil.hideLowLevelLogs();
        ExceptionUtil.setupUncaughtExceptionHandler();

        throwLazyInitializationException();
        throwNonUniqueObjectException();
        throwNonUniqueResultException();
        throwObjectDeletedException();
        throwPersistentObjectException();
        throwPropertyValueException();
        throwQuerySyntaxException();
        throwStaleStateException();
        throwUnknownEntityTypeException();
        System.out.println("=====END====================================================");
    }

    static void throwLazyInitializationException() {
        System.out.println("=====throwLazyInitializationException============================================");
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
            // yevhent.demo.hibernate.entity.artschool.ArtTeacher.artReviews:
            // could not initialize proxy - no Session
        }
    }

    static void throwNonUniqueObjectException() {
        System.out.println("=====throwNonUniqueObjectException============================================");
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
        System.out.println("=====throwNonUniqueResultException====================================================");
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

    static void throwObjectDeletedException() {
        System.out.println("=====throwObjectDeletedException====================================================");
        int id = ExceptionUtil.saveNewTeacherToDB();
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            ArtTeacher teacher = entityManager.find(ArtTeacher.class, id);
            entityManager.remove(teacher);
            entityManager.merge(teacher); // throws Exception
            entityManager.getTransaction().commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            // java.lang.IllegalArgumentException
            System.out.println(e.getCause());
            // org.hibernate.ObjectDeletedException:
            // deleted instance passed to merge: [yevhent.demo.hibernate.entity.artschool.ArtTeacher#<null>]
        }
    }

    static void throwPersistentObjectException() {
        System.out.println("=====throwPersistentObjectException====================================================");
        int id = ExceptionUtil.saveNewTeacherToDB();
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            ArtTeacher teacher = entityManager.find(ArtTeacher.class, id);
            entityManager.detach(teacher);
            entityManager.persist(teacher); // throws Exception
            entityManager.getTransaction().commit();
        } catch (EntityExistsException e) {
            System.out.println(e);
            // jakarta.persistence.EntityExistsException
            System.out.println(e.getCause());
            // org.hibernate.PersistentObjectException:
            // detached entity passed to persist: yevhent.demo.hibernate.entity.artschool.ArtTeacher
        }
    }

    static void throwPropertyValueException() {
        System.out.println("=====throwPropertyValueException====================================================");
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            MandatoryNamedItem mandatoryNamed = new MandatoryNamedItem();
            entityManager.persist(mandatoryNamed); // throws Exception
            entityManager.getTransaction().commit();
        } catch (PropertyValueException e) {
            System.out.println(e);
            // org.hibernate.PropertyValueException:
            // not-null property references a null or transient value:
            // yevhent.demo.hibernate.entity.general.MandatoryNamedItem.name
        }
    }

    static void throwQuerySyntaxException() {
        System.out.println("=====throwQuerySyntaxException====================================================");
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Query invalidQuery = entityManager.createQuery("SELECTING t FROM ArtTeacher t"); // throws Exception
            invalidQuery.getResultList();
            entityManager.getTransaction().commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            // java.lang.IllegalArgumentException
            System.out.println(e.getCause());
            // org.hibernate.query.SyntaxException:
            // At 1:0 and token 'SELECTING', no viable alternative at input '*SELECTING t FROM ArtTeacher t' [SELECTING t FROM ArtTeacher t]
        }
    }

    static void throwStaleStateException() {
        System.out.println("=====throwStaleStateException====================================================");
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
            // [yevhent.demo.hibernate.entity.general.VersionedItem#33]
            System.out.println(e.getCause().getCause());
            // org.hibernate.StaleObjectStateException:
            // Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect):
            // [yevhent.demo.hibernate.entity.general.VersionedItem#33]
        }
    }

    static void throwUnknownEntityTypeException() {
        System.out.println("=====throwUnknownEntityTypeException====================================================");
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            UnknownEntity unknownEntity = new UnknownEntity();
            entityManager.persist(unknownEntity); // throws Exception
            entityManager.getTransaction().commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            // java.lang.IllegalArgumentException
            System.out.println(e.getCause());
            // org.hibernate.UnknownEntityTypeException:
            // Unable to locate persister: yevhent.demo.hibernate.entity.general.UnknownEntity
        }
    }

    static void throwWrongClassException() {
        System.out.println("=====throwWrongClassException====================================================");
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            UnknownEntity unknownEntity = new UnknownEntity();
            entityManager.persist(unknownEntity); // throws Exception
            entityManager.getTransaction().commit();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            // java.lang.IllegalArgumentException
            System.out.println(e.getCause());
            // org.hibernate.UnknownEntityTypeException:
            // Unable to locate persister: yevhent.demo.hibernate.entity.general.UnknownEntity
        }
    }
}
