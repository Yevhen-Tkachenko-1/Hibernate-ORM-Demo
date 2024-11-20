package yevhent.demo.hibernate.relation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtClass;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bidirectional relation between Teacher and Review,
 * where `art_reviews` table has FK as reference to `art_teachers`
 * and ArtTeacher object contains list of ArtReview objects
 * and ArtReview object contains ArtTeacher object
 */
public class SelectOneToManyDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            // ArtTeacher with ID = 1 and related ArtReviews must be persisted in DB before running SelectOneToManyDemo
            ArtTeacher artTeacher = entityManager.find(ArtTeacher.class, 1);
            // Hibernate: select at1_0.teacher_id,at1_0.teacher_name from art_school.art_teachers at1_0 where at1_0.teacher_id=?
            List<ArtReview> artReviews = artTeacher.getArtReviews();
            // Hibernate: select ar1_0.teacher_id,ar1_0.review_id,ar1_0.review_comment,ar1_0.rating from art_school.art_reviews ar1_0 where ar1_0.teacher_id=?
            System.out.printf("Teacher %s has written\n%s\nwith corresponding ratings %s\n",
                    artTeacher.getName(),
                    artReviews.stream().map(ArtReview::getComment).collect(Collectors.toList()),
                    artReviews.stream().map(ArtReview::getRating).collect(Collectors.toList()));
            // Log output: Teacher John has written
            // [Review number 0, Review number 1, Review number 2, Review number 3, Review number 4, Review number 5, Review number 6, Review number 7, Review number 8, Review number 9]
            // with corresponding ratings [0, 10, 20, 30, 40, 50, 60, 70, 80, 90]
        }
    }
}