package yevhent.demo.hibernate.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtReview;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;
import yevhent.demo.hibernate.query.crud.CrudQuery;
import yevhent.demo.hibernate.query.crud.CrudQueryUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CriteriaQueryDemo implements CrudQuery {

    public static void main(String[] args) {

        CrudQuery demoQuery = new CriteriaQueryDemo();
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
        throw new UnsupportedOperationException("Insert operations are not designed in jakarta.persistence.criteria");
    }

    @Override
    public List<ArtReview> createReviews(int teacherId, List<Integer> ratings) {
        throw new UnsupportedOperationException("Insert operations are not designed in jakarta.persistence.criteria");
    }

    @Override
    public Map<String, Long> findTeachersWithReviewNumberMore(int teacherIdFrom, int teacherIdTo, int minNumberOfReviews) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = criteriaBuilder.createTupleQuery();
            Root<ArtTeacher> root = cq.from(ArtTeacher.class);
            Join<ArtTeacher, ArtReview> reviews = root.join("artReviews");
            cq.multiselect(
                    root.get("id").alias("id"),
                    root.get("name").alias("name"),
                    criteriaBuilder.count(reviews).alias("number")
            );
            cq.where(criteriaBuilder.between(root.get("id"), teacherIdFrom, teacherIdTo));
            cq.groupBy(root.get("id"), root.get("name"));
            cq.having(criteriaBuilder.greaterThan(criteriaBuilder.count(reviews), (long) minNumberOfReviews));
            TypedQuery<Tuple> query = entityManager.createQuery(cq);
            List<Tuple> results = query.getResultList();
            // Hibernate: select at1_0.teacher_id,at1_0.teacher_name,count(ar1_0.review_id)
            //            from art_school.art_teachers at1_0
            //            join art_school.art_reviews ar1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            where at1_0.teacher_id between ? and ?
            //            group by 1,2
            //            having count(ar1_0.review_id)>?
            Map<String, Long> teacherReviewNumbers = results.stream()
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
            entityManager.getTransaction().begin();

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<ArtReview> update = cb.createCriteriaUpdate(ArtReview.class);
            Root<ArtReview> reviewRoot = update.from(ArtReview.class);
            update.set(reviewRoot.<Number>get("rating"),
                            cb.prod(cb.quot(reviewRoot.get("rating"), 10), 10))
                    .where(cb.equal(reviewRoot.get("artTeacher").get("id"), teacherId));
            int updatedCount = entityManager.createQuery(update).executeUpdate();
            // Hibernate: update art_school.art_reviews ar1_0
            //            set rating=((ar1_0.rating/?)*?)
            //            where ar1_0.teacher_id=?
            System.out.printf("Rounded %d Reviews.\n", updatedCount);

            CriteriaQuery<Tuple> select = cb.createTupleQuery();
            Root<ArtReview> selectRoot = select.from(ArtReview.class);
            select.multiselect(
                    selectRoot.get("id").alias("id"),
                    selectRoot.get("comment").alias("comment"),
                    selectRoot.get("rating").alias("new_rating")
            ).where(cb.equal(selectRoot.get("artTeacher").get("id"), teacherId));
            List<Tuple> rows = entityManager.createQuery(select).getResultList();
            // Hibernate: select ar1_0.review_id,ar1_0.review_comment,ar1_0.rating
            //            from art_school.art_reviews ar1_0
            //            where ar1_0.teacher_id=?
            Map<String, Integer> reviews = rows.stream()
                    .peek(row -> System.out.printf("Updated Rating in Review(%d, \"%s\") to %d.\n",
                            row.get("id", Integer.class), row.get("comment", String.class), row.get("new_rating", Integer.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Review(%d, \"%s\")", row.get("id", Integer.class), row.get("comment", String.class)),
                            row -> row.get("new_rating", Integer.class)));
            entityManager.getTransaction().commit();
            return reviews;
        }
    }


    @Override
    public Map<String, Integer> deleteReviewsWithRatingLower(int teacherId, int minRating) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tuple> selectQuery = criteriaBuilder.createTupleQuery();
            Root<ArtReview> reviewRoot = selectQuery.from(ArtReview.class);
            selectQuery.multiselect(
                    reviewRoot.get("id").alias("id"),
                    reviewRoot.get("comment").alias("comment"),
                    reviewRoot.get("rating").alias("lower_rating")
            ).where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(reviewRoot.get("artTeacher").get("id"), teacherId),
                            criteriaBuilder.lt(reviewRoot.get("rating"), minRating)
                    )
            );
            List<Tuple> rows = entityManager.createQuery(selectQuery).getResultList();
            // Hibernate: select ar1_0.review_id,ar1_0.review_comment,ar1_0.rating
            //            from art_school.art_reviews ar1_0
            //            where ar1_0.teacher_id=? and ar1_0.rating<?
            CriteriaDelete<ArtReview> deleteQuery = criteriaBuilder.createCriteriaDelete(ArtReview.class);
            Root<ArtReview> deleteRoot = deleteQuery.from(ArtReview.class);
            deleteQuery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(deleteRoot.get("artTeacher").get("id"), teacherId),
                            criteriaBuilder.lt(deleteRoot.get("rating"), minRating)
                    )
            );
            int deletedCount = entityManager.createQuery(deleteQuery).executeUpdate();
            // Hibernate: delete from art_school.art_reviews ar1_0
            //            where ar1_0.teacher_id=? and ar1_0.rating<?
            System.out.printf("Deleted %d reviews.\n", deletedCount);

            entityManager.getTransaction().commit();

            Map<String, Integer> reviews = rows.stream()
                    .peek(row -> System.out.printf("Deleting Review(%d, \"%s\", %d).\n",
                            row.get("id", Integer.class), row.get("comment", String.class), row.get("lower_rating", Integer.class)))
                    .collect(Collectors.toMap(
                            row -> String.format("Review(%d, \"%s\")", row.get("id", Integer.class), row.get("comment", String.class)),
                            row -> row.get("lower_rating", Integer.class)));
            return reviews;
        }
    }
}
