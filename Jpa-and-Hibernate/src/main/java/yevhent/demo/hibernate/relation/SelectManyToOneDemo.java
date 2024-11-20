package yevhent.demo.hibernate.relation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
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
public class SelectManyToOneDemo {
    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            // ArtReview with ID = 1 and related ArtTeacher must be persisted in DB before running SelectManyToOneDemo
            ArtReview artReview = entityManager.find(ArtReview.class, 1);
            // Hibernate: select ar1_0.review_id,at1_0.teacher_id,at1_0.teacher_name,ar1_0.review_comment,ar1_0.rating
            //            from art_school.art_reviews ar1_0
            //            left join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            where ar1_0.review_id=?
            ArtTeacher artTeacher = artReview.getArtTeacher();
            System.out.printf("Art Assignment was mentioned as %s and rated as %s by %s\n",
                    artReview.getComment(), artReview.getRating(), artTeacher.getName());
            //Log output: Art Assignment was mentioned as Review number 0 and rated as 0 by John
        }
    }
}