package yevhent.demo.hibernate.exception;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TransactionRequiredException;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;

public class JpaExceptionsDemo {

    public static void main(String[] args) {

        ExceptionUtil.setupUncaughtExceptionHandler();

        System.out.println("=====throwEntityNotFoundException============================================");
        throwEntityNotFoundException();
        System.out.println("=====throwTransactionRequiredException====================================================");
        throwTransactionRequiredException();
        System.out.println("=====END====================================================");
    }

    static void throwEntityNotFoundException() {
        int teacherId = ExceptionUtil.saveNewTeacherAndReviewsToDB();
        ExceptionUtil.deleteTeacherAndReviewsFromDB(teacherId);

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            ArtTeacher teacherProxy = entityManager.getReference(ArtTeacher.class, teacherId);
            System.out.println(teacherProxy.getName()); // throws EntityNotFoundException
        } catch (EntityNotFoundException e) {
            System.out.println(e);
            // jakarta.persistence.EntityNotFoundException:
            // Unable to find yevhent.demo.hibernate.entity.artschool.ArtTeacher with id 320
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