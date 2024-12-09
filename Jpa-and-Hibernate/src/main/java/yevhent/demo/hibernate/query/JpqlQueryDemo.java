package yevhent.demo.hibernate.query;

import jakarta.persistence.*;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
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
            // Hibernate: select ar1_0.teacher_id,at1_0.teacher_name,count(ar1_0.review_id)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            where ar1_0.teacher_id between ? and ?
            //            group by ar1_0.teacher_id,at1_0.teacher_name
            //            having count(ar1_0.review_id)>?
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
            // Unlike Native query we have to begin transaction explicitly as it's required by persistent context
            entityManager.getTransaction().begin();

            String jpqlUpdate = """
                    UPDATE ArtReview r
                    SET r.rating = ROUND(r.rating / 10) * 10
                    WHERE r.artTeacher.id = :teacherId
                    """;
            Query updateQuery = entityManager.createQuery(jpqlUpdate);
            updateQuery.setParameter("teacherId", teacherId);
            int updatedCount = updateQuery.executeUpdate();
            // Hibernate: update art_school.art_reviews ar1_0
            //            set rating=(round((ar1_0.rating/10))*10)
            //            where ar1_0.teacher_id=?
            System.out.printf("Rounded %d Reviews.\n", updatedCount);

            String jpqlSelect = """
                    SELECT r.id AS id, r.comment AS comment, r.rating AS rating
                    FROM ArtReview r
                    WHERE r.artTeacher.id = :teacherId
                    """;
            TypedQuery<Tuple> selectQuery = entityManager.createQuery(jpqlSelect, Tuple.class);
            selectQuery.setParameter("teacherId", teacherId);
            List<Tuple> rows = selectQuery.getResultList();
            // Hibernate: select ar1_0.review_id,ar1_0.review_comment,ar1_0.rating
            //            from art_school.art_reviews ar1_0
            //            where ar1_0.teacher_id=?
            Map<String, Integer> updatedReviews = rows.stream()
                    .peek(row -> System.out.printf("Updated Rating in Review(%d, \"%s\") to %d.\n",
                            row.get("id", Integer.class), row.get("comment", String.class), row.get("rating", Integer.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Review(%d, \"%s\")", row.get("id", Integer.class), row.get("comment", String.class)),
                            row -> row.get("rating", Integer.class)));

            entityManager.getTransaction().commit();
            return updatedReviews;
        }
    }

    @Override
    public Map<String, Integer> deleteReviewsWithRatingLower(int teacherId, int minRating) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            String selectJpql = """
                    SELECT r.id AS id, r.comment AS comment, r.rating AS lowerRating
                    FROM ArtReview r
                    WHERE r.artTeacher.id = :teacherId AND r.rating < :minRating
                    """;
            TypedQuery<Tuple> selectQuery = entityManager.createQuery(selectJpql, Tuple.class);
            selectQuery.setParameter("teacherId", teacherId);
            selectQuery.setParameter("minRating", minRating);
            List<Tuple> rows = selectQuery.getResultList();
            // Hibernate: select ar1_0.review_id,ar1_0.review_comment,ar1_0.rating
            //            from art_school.art_reviews ar1_0
            //            where ar1_0.teacher_id=? and ar1_0.rating<?

            String deleteJpql = """
                    DELETE FROM ArtReview r
                    WHERE r.artTeacher.id = :teacherId AND r.rating < :minRating
                    """;
            Query deleteQuery = entityManager.createQuery(deleteJpql);
            deleteQuery.setParameter("teacherId", teacherId);
            deleteQuery.setParameter("minRating", minRating);
            deleteQuery.executeUpdate();
            // Hibernate: delete from art_school.art_reviews ar1_0
            //            where ar1_0.teacher_id=? and ar1_0.rating<?
            entityManager.getTransaction().commit();

            Map<String, Integer> deletedReviews = rows.stream()
                    .peek(row -> System.out.printf("Deleted Review(%d, \"%s\", %d).\n",
                            row.get("id", Integer.class), row.get("comment", String.class), row.get("lowerRating", Integer.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Review(%d, \"%s\")", row.get("id", Integer.class), row.get("comment", String.class)),
                            row -> row.get("lowerRating", Integer.class)));
            return deletedReviews;
        }
    }
}
