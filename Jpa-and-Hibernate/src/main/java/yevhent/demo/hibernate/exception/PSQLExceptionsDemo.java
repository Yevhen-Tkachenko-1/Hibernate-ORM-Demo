package yevhent.demo.hibernate.exception;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.general.SelfIdentifiable;

import java.util.Map;

public class PSQLExceptionsDemo {

    public static void main(String[] args) {
        ExceptionUtil.hideLowLevelLogs();
        ExceptionUtil.setupUncaughtExceptionHandler();

        System.out.println("=====throwConstraintViolationException====================================================");
        throwConstraintViolationException();
        System.out.println("=====throwLockTimeoutException============================================");
        throwLockTimeoutException();
        System.out.println("=====throwPessimisticLockException====================================================");
        throwPessimisticLockException();
        System.out.println("=====throwQueryTimeoutException====================================================");
        throwQueryTimeoutException();
        System.out.println("=====END====================================================");
    }

    static void throwConstraintViolationException() {
        int id = 1;
        ExceptionUtil.saveSelfIdentifiableToDBIfNotPresent(id);
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(new SelfIdentifiable(id)); // throws Exception
            entityManager.getTransaction().commit();
        } catch (RollbackException e) {
            System.out.println(e);
            // jakarta.persistence.RollbackException:
            // Error while committing the transaction
            System.out.println(e.getCause());
            // org.hibernate.exception.ConstraintViolationException:
            // could not execute statement [insert into public.self_assigned_ids (identity_id) values (?)]
            System.out.println(e.getCause().getCause());
            // org.postgresql.util.PSQLException:
            // ERROR: duplicate key value violates unique constraint "self_assigned_ids_pkey"
            // Detail: Key (identity_id)=(1) already exists.
        }
    }

    static void throwLockTimeoutException() {
        ExceptionUtil.accessArtTeacherWithPessimisticLock(
                "SET lock_timeout = 2000",
                Map.of("jakarta.persistence.lock.timeout", 2000L),
                e -> {
                    System.out.println(e);
                    // jakarta.persistence.LockTimeoutException:
                    // JDBC exception executing SQL [select at1_0.teacher_id,at1_0.teacher_name from art_school.art_teachers at1_0 where at1_0.teacher_id=? for no key update]
                    System.out.println(e.getCause());
                    // org.hibernate.PessimisticLockException:
                    // JDBC exception executing SQL [select at1_0.teacher_id,at1_0.teacher_name from art_school.art_teachers at1_0 where at1_0.teacher_id=? for no key update]
                    System.out.println(e.getCause().getCause());
                    // org.postgresql.util.PSQLException:
                    // ERROR: canceling statement due to lock timeout
                    // Where: while locking tuple (2,88) in relation "art_teachers"
                }
        );
    }

    static void throwPessimisticLockException() {
        ExceptionUtil.accessArtTeacherWithPessimisticLock(
                "SET lock_timeout = '2s'",
                Map.of(),
                e -> {
                    System.out.println(e);
                    // jakarta.persistence.PessimisticLockException:
                    // JDBC exception executing SQL [select at1_0.teacher_id,at1_0.teacher_name from art_school.art_teachers at1_0 where at1_0.teacher_id=? for no key update]
                    System.out.println(e.getCause());
                    // org.hibernate.PessimisticLockException:
                    // JDBC exception executing SQL [select at1_0.teacher_id,at1_0.teacher_name from art_school.art_teachers at1_0 where at1_0.teacher_id=? for no key update]
                    System.out.println(e.getCause().getCause());
                    // org.postgresql.util.PSQLException:
                    // ERROR: canceling statement due to lock timeout Where: while locking tuple (2,21) in relation "art_teachers"
                }
        );
    }

    static void throwQueryTimeoutException() {
        ExceptionUtil.accessArtTeacherWithPessimisticLock(
                "SET statement_timeout = '2s'",
                Map.of(),
                e -> {
                    System.out.println(e);
                    // jakarta.persistence.QueryTimeoutException:
                    // JDBC exception executing SQL [select at1_0.teacher_id,at1_0.teacher_name from art_school.art_teachers at1_0 where at1_0.teacher_id=? for no key update]
                    System.out.println(e.getCause());
                    // org.hibernate.QueryTimeoutException:
                    // JDBC exception executing SQL [select at1_0.teacher_id,at1_0.teacher_name from art_school.art_teachers at1_0 where at1_0.teacher_id=? for no key update]
                    System.out.println(e.getCause().getCause());
                    // org.postgresql.util.PSQLException:
                    // ERROR: canceling statement due to statement timeout
                    // Where: while locking tuple (2,53) in relation "art_teachers"
                }
        );
    }
}