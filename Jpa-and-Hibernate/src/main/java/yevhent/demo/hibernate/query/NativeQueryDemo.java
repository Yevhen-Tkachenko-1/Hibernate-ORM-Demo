package yevhent.demo.hibernate.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NativeQueryDemo implements CrudQuery {

    public static void main(String[] args) {

        CrudQuery crudQuery = new NativeQueryDemo();
        Map<ArtTeacher, List<ArtReview>> entities = CrudQueryUser.create(crudQuery);

        CrudQueryUser.read(crudQuery);
    }

    @Override
    public ArtTeacher createTeacher(String name) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String sql = "INSERT INTO art_school.art_teachers (teacher_name)" +
                    " VALUES (:name)" +
                    " RETURNING *";
            Query query = entityManager.createNativeQuery(sql, ArtTeacher.class);
            query.setParameter("name", name);
            ArtTeacher teacher = (ArtTeacher) query.getSingleResult();
            System.out.println("Created " + teacher);
            return teacher;
        }
    }

    @Override
    public List<ArtReview> createReviews(int teacherId, List<Integer> ratings) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            List<ArtReview> reviews = ratings.stream().map(rating -> {
                String sql = "INSERT INTO art_school.art_reviews (review_comment, rating, teacher_id)" +
                        " VALUES (:comment, :rating, :teacherId)" +
                        " RETURNING *"; // review_id, review_comment, rating and ArtTeacher entity
                Query query = entityManager.createNativeQuery(sql, ArtReview.class);
                query.setParameter("comment", "Commented by Teacher[" + teacherId + "] and rated as " + rating);
                query.setParameter("rating", rating);
                query.setParameter("teacherId", teacherId);
                ArtReview review = (ArtReview) query.getSingleResult();
                System.out.println("Created " + review + " for "+ review.getArtTeacher());
                return review;
            }).collect(Collectors.toList());
            return reviews;
        }
    }

    @Override
    public Map<String, Long> findTeachersWithReviewNumber(int minNumberOfReviews) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String sql = "SELECT t.teacher_name AS name, COUNT(*) AS number ";
            sql += " FROM art_school.art_teachers AS t";
            sql += " JOIN art_school.art_reviews AS r ON t.teacher_id = r.teacher_id";
            sql += " GROUP BY t.teacher_id, t.teacher_name";
            sql += " HAVING COUNT(*) > :minNumber";
            Query query = entityManager.createNativeQuery(sql, Tuple.class);
            query.setParameter("minNumber", minNumberOfReviews);
            List<Tuple> rows = query.getResultList();
            Map<String, Long> teacherReviewNumbers = rows.stream()
                    .peek(row -> System.out.println("Found Teacher " + row.get("name", String.class) + " with " + row.get("number", Long.class) + " reviews"))
                    .collect(Collectors.toMap(row -> row.get("name", String.class), row -> row.get("number", Long.class)));
            return teacherReviewNumbers;
        }
    }

    @Override
    public Map<String, Double> findAverageReviewsRatings(int minAverageRating) {
        return null;
    }

    @Override
    public Map<String, List<Integer>> roundReviewRatings(int teacherId) {
        return null;
    }

    @Override
    public int deleteReviewsLower(int teacherId, int rating) {
        return 0;
    }
}