package yevhent.demo.hibernate.query;

import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;

import java.util.List;
import java.util.Map;

public interface CrudQuery {

    /**
     * CREATE
     */
    ArtTeacher createTeacher(String name);
    /**
     * CREATE
     */
    List<ArtReview> createReviews(int teacherId, List<Integer> ratings);
    /**
     * READ
     */
    Map<String, Long> findTeachersWithReviewNumber(int minNumberOfReviews);
    /**
     * READ
     */
    Map<String, Double> findAverageReviewsRatings(int minAverageRating);

    /**
     * UPDATE
     */
    Map<String, List<Integer>> roundReviewRatings(int teacherId);
    /**
     * DELETE
     */
    int deleteReviewsLower(int teacherId, int rating);

}