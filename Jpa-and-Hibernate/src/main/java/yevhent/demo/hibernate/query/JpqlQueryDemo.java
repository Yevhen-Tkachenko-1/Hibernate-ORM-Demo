package yevhent.demo.hibernate.query;

import jakarta.persistence.*;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtClass;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;
import yevhent.demo.hibernate.query.crud.CrudQuery;
import yevhent.demo.hibernate.query.crud.CrudQueryUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JpqlQueryDemo implements CrudQuery {

    public static void main(String[] args) {

        CrudQuery demoQuery = new JpqlQueryDemo();
        Map<ArtTeacher, List<ArtReview>> entities;
        try {
            entities = CrudQueryUser.create(demoQuery);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            entities = CrudQueryUser.create(new NativeQueryDemo());
        }
        CrudQueryUser.read(demoQuery, entities);
        CrudQueryUser.update(demoQuery, entities);
        CrudQueryUser.delete(demoQuery, entities);
    }

    @Override
    public ArtTeacher createTeacher(String name) {
        throw new UnsupportedOperationException("Insert operations are not designed in jakarta.persistence JPQL");
    }

    @Override
    public List<ArtReview> createReviews(int teacherId, List<Integer> ratings) {
        throw new UnsupportedOperationException("Insert operations are not designed in jakarta.persistence JPQL");
    }

    @Override
    public Map<String, Long> findTeachersWithReviewNumberMore(int teacherIdFrom, int teacherIdTo, int minNumberOfReviews) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String jpql = """
                    SELECT r.artTeacher.id AS id, r.artTeacher.name AS name, COUNT(r) AS number
                    FROM ArtReview r
                    WHERE r.artTeacher.id BETWEEN :teacherIdFrom AND :teacherIdTo
                    GROUP BY r.artTeacher.id, r.artTeacher.name
                    HAVING COUNT(r) > :minNumber
                    """;
            TypedQuery<Tuple> query = entityManager.createQuery(jpql, Tuple.class);
            query.setParameter("teacherIdFrom", teacherIdFrom);
            query.setParameter("teacherIdTo", teacherIdTo);
            query.setParameter("minNumber", minNumberOfReviews);
            List<Tuple> rows = query.getResultList();
            Map<String, Long> teacherReviewNumbers = rows.stream()
                    .peek(row -> System.out.printf("Found Teacher(%d, \"%s\") with %s reviews.\n",
                            row.get("id", Integer.class), row.get("name", String.class), row.get("number", Long.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Teacher(%d, \"%s\")", row.get("id", Integer.class), row.get("name", String.class)),
                            row -> row.get("number", Long.class)));
            return teacherReviewNumbers;
        }
    }

    @Override
    public Map<String, Integer> roundReviewRatings(int teacherId) {

//        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
//             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
//            String jpqlUpdate = """
//                UPDATE ArtReview r
//                SET r.rating = ROUND(r.rating / 10) * 10
//                WHERE r.artTeacher.id = :teacherId
//                """;
//            TypedQuery<Tuple> query = entityManager.createQuery(jpqlUpdate, Tuple.class);
//            query.setParameter("teacherId", teacherId);
//            List<Tuple> rows = query.getResultList();
//            Map<String, Integer> reviews = rows.stream()
//                    .peek(row -> System.out.printf("Updated Rating in Review(%d, \"%s\") to %d.\n",
//                            row.get("id", Integer.class), row.get("comment", String.class), row.get("new_rating", Integer.class)))
//                    .collect(Collectors.toMap(
//                            row -> String.format("Review(%d, \"%s\")", row.get("id", Integer.class), row.get("comment", String.class)),
//                            row -> row.get("new_rating", Integer.class)));
//            return reviews;
//        }
//
//        Query updateQuery = entityManager.createQuery(jpqlUpdate);
//        updateQuery.setParameter("teacherId", teacherId);
//        int updatedCount = updateQuery.executeUpdate();
//        System.out.println("Number of reviews updated: " + updatedCount);
//
//        // Step 2: Retrieve the updated reviews and their details
//        String jpqlSelect = """
//                SELECT r.id AS id, r.reviewComment AS comment, r.rating AS new_rating
//                FROM ArtReview r
//                WHERE r.artTeacher.id = :teacherId
//        """;
       return null;
    }

    @Override
    public Map<String, Integer> deleteReviewsWithRatingLower(int teacherId, int rating) {
        return null;
    }

    public static void findStudentClasses() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT s.artClasses FROM ArtStudent s WHERE s.name = :studentName";
            TypedQuery<ArtClass> q1 = entityManager.createQuery(s1, ArtClass.class)
                    .setParameter("studentName", "John");
            List<ArtClass> artClasses = q1.getResultList();
            // Hibernate: select ac1_1.class_id,ac1_1.teacher_id,ac1_1.class_name,ac1_1.week_day
            //            from art_school.art_students as1_0
            //            join art_school.students_classes_mapping ac1_0 on as1_0.student_id=ac1_0.student_id
            //            join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            where as1_0.student_name='John'
            // Select query to DB
            System.out.println("Student John attends " + artClasses);
            // Student John attends [ArtClass(id=5, name=Painting, weekDay=Monday), ArtClass(id=6, name=Music, weekDay=Saturday)]
        }
    }

    public static void findTeacherAverageReviewRating() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT AVG(r.rating) FROM ArtReview r WHERE r.artTeacher.name = :teacherName";
            TypedQuery<Double> q1 = entityManager.createQuery(s1, Double.class)
                    .setParameter("teacherName", "John");
            Double averageRating = q1.getSingleResult();
            // Hibernate: select avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            where at1_0.teacher_name='John'
            // Select query to DB
            System.out.println("Teacher John rated in average as " + averageRating);
            // Teacher John rated in average as 45.0
        }
    }

    public static void findTeachersAverageReviewRatings() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT t.name, AVG(r.rating) FROM ArtReview r JOIN r.artTeacher t GROUP BY t.name";
            TypedQuery<Object[]> q1 = entityManager.createQuery(s1, Object[].class);
            List<Object[]> averageRatings = q1.getResultList();
            // Hibernate: select at1_0.teacher_name,avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            group by at1_0.teacher_name
            // Select query to DB
            for (Object[] averageRating : averageRatings) {
                System.out.printf("Teacher %s rated in average as %s\n", averageRating[0], averageRating[1]);
            }
            // Teacher Mike rated in average as 45.0
            // Teacher Joe rated in average as 45.0
            // Teacher John rated in average as 45.0
        }
    }

    public static void findTeachersAverageReviewRatingsGreaterThan40() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT t.name, AVG(r.rating) FROM ArtReview r JOIN r.artTeacher t GROUP BY t.name HAVING AVG(r.rating) > 40 ORDER BY AVG(r.rating) DESC";
            TypedQuery<Object[]> q1 = entityManager.createQuery(s1, Object[].class);
            List<Object[]> averageRatings = q1.getResultList();
            // Hibernate: select at1_0.teacher_name,avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            group by at1_0.teacher_name
            //            having avg(ar1_0.rating)>40
            //            order by avg(ar1_0.rating) desc
            // Select query to DB
            for (Object[] averageRating : averageRatings) {
                System.out.printf("Teacher %s rated in average as %s\n", averageRating[0], averageRating[1]);
            }
            // Teacher Mike rated in average as 45.0
            // Teacher Joe rated in average as 45.0
        }
    }

    public static void deleteReviewsRatingsLowerThan40(int teacherId) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "DELETE FROM t.name, AVG(r.rating) FROM ArtReview r JOIN r.artTeacher t GROUP BY t.name HAVING AVG(r.rating) > 40 ORDER BY AVG(r.rating) DESC";
            TypedQuery<Object[]> q1 = entityManager.createQuery(s1, Object[].class);
            List<Object[]> averageRatings = q1.getResultList();
            // Hibernate: select at1_0.teacher_name,avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            group by at1_0.teacher_name
            //            having avg(ar1_0.rating)>40
            //            order by avg(ar1_0.rating) desc
            // Select query to DB
            for (Object[] averageRating : averageRatings) {
                System.out.printf("Teacher %s rated in average as %s\n", averageRating[0], averageRating[1]);
            }
            // Teacher Mike rated in average as 45.0
            // Teacher Joe rated in average as 45.0
        }
    }
}
