package yevhent.demo.hibernate.relation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Bidirectional relation between Teacher and Review,
 * where `art_reviews` table has FK as reference to `art_teachers`
 * and ArtTeacher object contains list of ArtReview objects
 * and ArtReview object contains ArtTeacher object
 */
public class InsertOneToManyDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            ArtTeacher artTeacher = new ArtTeacher(0, "John");
            List<ArtReview> artReviews = IntStream.range(0, 10).boxed()
                    .map(i -> new ArtReview(0, "Review number " + i + " by Teacher " + artTeacher.getName(), i * 10, artTeacher))
                    .collect(Collectors.toList());
            artTeacher.setArtReviews(artReviews);

            entityManager.persist(artTeacher);
            // Hibernate: insert into art_school.art_teachers (teacher_name) values (?) returning teacher_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            // Hibernate: insert into art_school.art_reviews (teacher_id,review_comment,rating) values (?,?,?) returning review_id
            entityManager.getTransaction().commit();
            // insert ArtTeacher to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
            // insert ArtReview to DB
        }
    }
}