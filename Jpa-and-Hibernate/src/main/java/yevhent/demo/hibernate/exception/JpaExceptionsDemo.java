package yevhent.demo.hibernate.exception;

import jakarta.persistence.*;
import lombok.SneakyThrows;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtTeacher;

import java.util.HashMap;
import java.util.Map;

public class JpaExceptionsDemo {

    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("===== Uncaught exception in thread \"" + t.getName() + "\":");
            e.printStackTrace();
        });
        try {
            System.out.println("=====throwEntityNotFoundException============================================");
            throwEntityNotFoundException();
            System.out.println("=====throwTransactionRequiredException====================================================");
            throwTransactionRequiredException();
            System.out.println("=====throwLockTimeoutException====================================================");
            throwLockTimeoutException();
            System.out.println("=====END====================================================");
        } catch (Exception e) {
            System.out.println("===== Caught exception in main thread:");
            e.printStackTrace();
        }
    }

    static void throwEntityNotFoundException() {

        int teacherId = ExceptionRepository.saveNewTeacherAndReviewsToDB();
        ExceptionRepository.deleteTeacherAndReviewsFromDB(teacherId);

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            ArtTeacher teacherProxy = entityManager.getReference(ArtTeacher.class, teacherId);
            System.out.println(teacherProxy.getName()); // throws EntityNotFoundException
        } catch (EntityNotFoundException e) {
            System.out.println(e);
            // jakarta.persistence.EntityNotFoundException:
            // Unable to find yevhent.demo.hibernate.entity.ArtTeacher with id 320
        }
    }

    @SneakyThrows(InterruptedException.class)
    static void throwLockTimeoutException() {
        Map<String, Object> props = new HashMap();
        props.put("javax.persistence.lock.timeout", 10_000L);
        props.put("jakarta.persistence.lock.timeout", 10_000L);
        props.put("hibernate.transaction.timeout", 10);

        int id = ExceptionRepository.saveNewTeacherAndReviewsToDB();

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager1 = entityManagerFactory.createEntityManager();
             EntityManager entityManager2 = entityManagerFactory.createEntityManager()) {
            entityManager2.setProperty("javax.persistence.lock.timeout", 10_000L);
            entityManager2.setProperty("jakarta.persistence.lock.timeout", 10_000L);
            Thread transaction1 = new Thread(() -> {
                System.out.println("Transaction 1 started.");
                entityManager1.getTransaction().begin();
                ArtTeacher teacher1 = entityManager1.find(ArtTeacher.class, id, LockModeType.PESSIMISTIC_WRITE);
                System.out.println("Transaction 1 acquired lock on: " + teacher1);
                System.out.println("Transaction 1 sleeps ...");
                try {
                    // Keep Transaction 1 open to block the lock
                    Thread.sleep(70000); // Simulate a long transaction
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Transaction 1 commits ...");
                entityManager1.getTransaction().commit();
                System.out.println("Transaction 1 ended.");
            });
            Thread transaction2 = new Thread(() -> {
                System.out.println("Transaction 2 started.");
                try {
                    entityManager2.getTransaction().begin();
                    System.out.println("Transaction 2 attempting to acquire lock...");
                    ArtTeacher teacher2 = entityManager2.find(ArtTeacher.class, id, LockModeType.PESSIMISTIC_WRITE, props);
                    System.out.println("Transaction 2 acquired lock on: " + teacher2);
                    entityManager2.getTransaction().commit();
                    System.out.println("Transaction 2 ended.");
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println(e.getCause());
                    System.out.println(e.getCause().getCause());
                } finally {
                    if (entityManager2.getTransaction().isActive()) {
                        System.out.println("isActive=true, rollback");
                        entityManager2.getTransaction().rollback();
                    }
                }
            });
            transaction1.start();
            transaction2.start();
            transaction1.join();
            transaction2.join();
            System.out.println("throwLockTimeoutException end");
        }
    }

    static void throwTransactionRequiredException() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            ArtTeacher teacher = new ArtTeacher("Prof. Davis");
            entityManager.persist(teacher);
            entityManager.flush(); // throws TransactionRequiredException
        } catch (TransactionRequiredException e) {
            System.out.println(e);
            // jakarta.persistence.TransactionRequiredException: no transaction is in progress
        }
    }
}