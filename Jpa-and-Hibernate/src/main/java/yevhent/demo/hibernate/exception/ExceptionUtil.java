package yevhent.demo.hibernate.exception;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import lombok.SneakyThrows;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtReview;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;
import yevhent.demo.hibernate.entity.general.SelfIdentifiable;
import yevhent.demo.hibernate.entity.general.VersionedItem;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ExceptionUtil {

    static int saveNewTeacherToDB() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            ArtTeacher teacher = new ArtTeacher("Prof. Davis");
            entityManager.persist(teacher);
            entityManager.getTransaction().commit();
            return teacher.getId();
        }
    }

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
            artTeacher.getArtReviews().forEach(entityManager::remove);
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

    static void setupUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("===== Uncaught exception in thread \"" + t.getName() + "\":");
            e.printStackTrace();
        });
    }

    static Logger sqlExceptionHelper;
    static Logger defaultLoadEventListener;

    static void hideLowLevelLogs() {
        // must be declared as strong references:
        sqlExceptionHelper = Logger.getLogger("org.hibernate.engine.jdbc.spi.SqlExceptionHelper");
        defaultLoadEventListener = Logger.getLogger("org.hibernate.event.internal.DefaultLoadEventListener");
        // and set after declared:
        sqlExceptionHelper.setLevel(Level.OFF);
        defaultLoadEventListener.setLevel(Level.OFF);
        // Don't work with weak referencing like this:
        // Logger.getLogger("org.hibernate.engine.jdbc.spi.SqlExceptionHelper").setLevel(Level.OFF);
    }

    @SneakyThrows(InterruptedException.class)
    static void accessArtTeacherWithPessimisticLock(String psqlTimeoutQuery, Map<String, Object> hints, Consumer<Exception> exceptionHandler) {
        int id = saveNewTeacherAndReviewsToDB();
        long start = System.currentTimeMillis();

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager1 = entityManagerFactory.createEntityManager();
             EntityManager entityManager2 = entityManagerFactory.createEntityManager()) {

            Thread transaction1 = new Thread(() -> {
                System.out.println((System.currentTimeMillis() - start) + " Transaction-1 started.");
                entityManager1.getTransaction().begin();
                System.out.println((System.currentTimeMillis() - start) + " Transaction-1 attempting to acquire lock...");
                ArtTeacher teacher1 = entityManager1.find(ArtTeacher.class, id, LockModeType.PESSIMISTIC_WRITE);
                System.out.println((System.currentTimeMillis() - start) + " Transaction-1 acquired lock on: " + teacher1);
                try {
                    int seconds = 5;
                    System.out.println((System.currentTimeMillis() - start) + " Transaction-1 simulates long processing and keeps lock for " + seconds + " seconds ...");
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println((System.currentTimeMillis() - start) + " Transaction-1 commits ...");
                entityManager1.getTransaction().commit();
                System.out.println((System.currentTimeMillis() - start) + " Transaction-1 ended.");
            });
            Thread transaction2 = new Thread(() -> {
                System.out.println((System.currentTimeMillis() - start) + " Transaction-2 started.");
                System.out.println((System.currentTimeMillis() - start) + " Transaction-2 waits to let Transaction-1 acquire lock ...");
                try {
                    int seconds = 1;
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    entityManager2.getTransaction().begin();
                    entityManager2.createNativeQuery(psqlTimeoutQuery).executeUpdate();
                    System.out.println((System.currentTimeMillis() - start) + " Transaction-2 attempting to acquire lock...");
                    ArtTeacher teacher2 = entityManager2.find(ArtTeacher.class, id, LockModeType.PESSIMISTIC_WRITE, hints); // throws Exception
                    // By calling entityManager2.find() Transaction-2 waits too long and throws Exception
                    System.out.println((System.currentTimeMillis() - start) + " Transaction-2 acquired lock on: " + teacher2);
                    entityManager2.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println((System.currentTimeMillis() - start) + " Transaction-2 failed:");
                    exceptionHandler.accept(e);
                } finally {
                    if (entityManager2.getTransaction().isActive()) {
                        System.out.println((System.currentTimeMillis() - start) + " Transaction-2 is active, rolling back ...");
                        entityManager2.getTransaction().rollback();
                    }
                }
            });
            transaction1.start();
            transaction2.start();
            transaction1.join();
            transaction2.join();
            System.out.println((System.currentTimeMillis() - start) + " ended.");
        }
    }
}
