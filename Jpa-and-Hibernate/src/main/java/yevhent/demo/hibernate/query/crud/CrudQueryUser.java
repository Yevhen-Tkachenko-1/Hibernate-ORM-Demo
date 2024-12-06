package yevhent.demo.hibernate.query.crud;

import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtTeacher;
import yevhent.demo.hibernate.query.crud.CrudQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CrudQueryUser {

    public static Map<ArtTeacher, List<ArtReview>> create(CrudQuery crudQuery) {
        List<String> teacherNames = List.of("Mr. Smith", "Ms. Johnson", "Dr. Miller", "Prof. Davis", "Mrs. Williams");
        Map<ArtTeacher, List<ArtReview>> teachersReviews = new HashMap();
        for (String teacherName : teacherNames) {
            ArtTeacher teacher = crudQuery.createTeacher(teacherName);
            List<ArtReview> reviews = crudQuery.createReviews(teacher.getId(), getRandomRatings());
            teachersReviews.put(teacher, reviews);
        }
        return teachersReviews;
    }

    public static void read(CrudQuery crudQuery, Map<ArtTeacher, List<ArtReview>> entities) {
        crudQuery.findTeachersWithReviewNumberMore(
                entities.keySet().stream().mapToInt(ArtTeacher::getId).min().orElse(0),
                entities.keySet().stream().mapToInt(ArtTeacher::getId).max().orElse(0),
                5);
    }

    public static void update(CrudQuery crudQuery, Map<ArtTeacher, List<ArtReview>> entities) {
        crudQuery.roundReviewRatings(entities.keySet().iterator().next().getId());
    }

    public static void delete(CrudQuery crudQuery, Map<ArtTeacher, List<ArtReview>> entities) {
        crudQuery.deleteReviewsWithRatingLower(entities.keySet().iterator().next().getId(), 30);
    }

    private static List<Integer> getRandomRatings() {
        int total = ThreadLocalRandom.current().nextInt(3, 11);
        return new Random().ints(total, 1, 100).boxed().collect(Collectors.toList());
    }
}