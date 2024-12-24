package yevhent.demo.hibernate.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtReview;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;
import yevhent.demo.hibernate.query.crud.CrudQuery;
import yevhent.demo.hibernate.query.crud.CrudQueryUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NativeQueryDemo implements CrudQuery {

    public static void main(String[] args) {

        CrudQuery demoQuery = new NativeQueryDemo();

        Map<ArtTeacher, List<ArtReview>> entities = CrudQueryUser.create(demoQuery);
        CrudQueryUser.read(demoQuery, entities);
        CrudQueryUser.update(demoQuery, entities);
        CrudQueryUser.delete(demoQuery, entities);
    }

    @Override
    public ArtTeacher createTeacher(String name) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String sql = """
                    INSERT INTO art_school.art_teachers (teacher_name)
                    VALUES (:name)
                    RETURNING *;
                    """;
            Query query = entityManager.createNativeQuery(sql, ArtTeacher.class);
            query.setParameter("name", name);
            ArtTeacher teacher = (ArtTeacher) query.getSingleResult();
            System.out.printf("Created %s.\n", teacher);
            return teacher;
        }
    }

    @Override
    public List<ArtReview> createReviews(int teacherId, List<Integer> ratings) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            List<ArtReview> reviews = ratings.stream().map(rating -> {
                String sql = """
                        INSERT INTO art_school.art_reviews (review_comment, rating, teacher_id)
                        VALUES (:comment, :rating, :teacherId)
                        RETURNING *;
                        """;
                Query query = entityManager.createNativeQuery(sql, ArtReview.class);
                query.setParameter("comment", String.format("Commented by Teacher(%d) and rated as %d.", teacherId, rating));
                query.setParameter("rating", rating);
                query.setParameter("teacherId", teacherId);
                ArtReview review = (ArtReview) query.getSingleResult();
                System.out.printf("Created %s related to %s.\n", review, review.getArtTeacher());
                return review;
            }).collect(Collectors.toList());
            return reviews;
        }
    }

    @Override
    public Map<String, Long> findTeachersWithReviewNumberMore(int teacherIdFrom, int teacherIdTo, int minNumberOfReviews) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String sql = """
                    SELECT t.teacher_id AS id, t.teacher_name AS name, COUNT(*) AS number
                    FROM art_school.art_teachers AS t
                    JOIN art_school.art_reviews AS r ON t.teacher_id = r.teacher_id
                    WHERE t.teacher_id BETWEEN :teacherIdFrom AND :teacherIdTo
                    GROUP BY t.teacher_id, t.teacher_name
                    HAVING COUNT(*) > :minNumber
                    """;
            Query query = entityManager.createNativeQuery(sql, Tuple.class);
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
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String sql = """
                    UPDATE art_school.art_reviews
                    SET rating = ROUND(rating / 10) * 10
                    WHERE teacher_id = :teacherId
                    RETURNING review_id AS id, review_comment AS comment, rating AS new_rating;
                    """;
            Query query = entityManager.createNativeQuery(sql, Tuple.class);
            query.setParameter("teacherId", teacherId);
            List<Tuple> rows = query.getResultList();
            Map<String, Integer> reviews = rows.stream()
                    .peek(row -> System.out.printf("Updated Rating in Review(%d, \"%s\") to %d.\n",
                            row.get("id", Integer.class), row.get("comment", String.class), row.get("new_rating", Integer.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Review(%d, \"%s\")", row.get("id", Integer.class), row.get("comment", String.class)),
                            row -> row.get("new_rating", Integer.class)));
            return reviews;
        }
    }

    @Override
    public Map<String, Integer> deleteReviewsWithRatingLower(int teacherId, int minRating) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String sql = """
                    WITH deleted_reviews AS (
                        DELETE FROM art_school.art_reviews
                        WHERE teacher_id = :teacherId AND rating < :minRating
                        RETURNING review_id AS id, review_comment AS comment, rating AS lower_rating
                    )
                    SELECT id, comment, lower_rating FROM deleted_reviews;
                    """;
            Query query = entityManager.createNativeQuery(sql, Tuple.class);
            query.setParameter("teacherId", teacherId);
            query.setParameter("minRating", minRating);
            List<Tuple> rows = query.getResultList();
            Map<String, Integer> reviews = rows.stream()
                    .peek(row -> System.out.printf("Deleted Review(%d, \"%s\", %d).\n",
                            row.get("id", Integer.class), row.get("comment", String.class), row.get("lower_rating", Integer.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Review(%d, \"%s\")", row.get("id", Integer.class), row.get("comment", String.class)),
                            row -> row.get("lower_rating", Integer.class)));
            return reviews;
        }
    }
}