package yevhent.demo.hibernate.query.crud;

import yevhent.demo.hibernate.entity.artschool.ArtReview;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;

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
    Map<String, Long> findTeachersWithReviewNumberMore(int teacherIdFrom, int teacherIdTo, int minNumberOfReviews);

    /**
     * UPDATE
     */
    Map<String, Integer> roundReviewRatings(int teacherId);

    /**
     * DELETE
     */
    Map<String, Integer> deleteReviewsWithRatingLower(int teacherId, int rating);

}