package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import yevhent.demo.hibernate.entity.artschool.ArtReview;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ArtSchoolService {

    private final ArtTeacherRepository teacherRepository;

    private final EntityManagerFactory entityManagerFactory;

    public List<ArtTeacher> getTeachersWithReviews(List<Integer> teacherIds) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return teacherRepository.findEagerTeachersByIds(entityManager, teacherIds);
        }
    }

    public List<Integer> saveTeachersWithReviews(Map<String, List<Integer>> teacherRequest) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            List<ArtTeacher> persistedTeachers = teacherRequest.keySet().stream()
                    .map(name -> teacherRepository.create(entityManager, new ArtTeacher(name))).toList();
            entityManager.flush();

            persistedTeachers.forEach(teacher -> {
                List<Integer> ratings = teacherRequest.get(teacher.getName());
                List<ArtReview> reviews = ratings.stream()
                        .map(rating -> new ArtReview(getComment(teacher, rating), rating, teacher)).toList();
                teacher.setArtReviews(reviews);
            });
            entityManager.getTransaction().commit();
            return persistedTeachers.stream().map(ArtTeacher::getId).toList();
        }
    }

    public List<Integer> processTeachersReviews(List<Integer> teacherIds, int minReviewNumber, int minRating) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();

            List<Integer> processedTeacherIds = teacherRepository.findEagerTeachersByIds(entityManager, teacherIds).stream()
                    .filter(teacher -> teacher.getArtReviews().size() > minReviewNumber)
                    .peek(teacher -> processReviews(entityManager, teacher, minRating))
                    .map(ArtTeacher::getId).toList();

            entityManager.getTransaction().commit();
            return processedTeacherIds;
        }
    }

    private void processReviews(EntityManager entityManager, ArtTeacher teacher, int minRating) {
        // delete low rating Reviews
        List<ArtReview> reviewsToDelete = teacher.getArtReviews().stream().filter(review -> review.getRating() < minRating).toList();
        reviewsToDelete.forEach(entityManager::remove);
        teacher.getArtReviews().removeAll(reviewsToDelete);
        // update rating to round value
        teacher.getArtReviews().forEach(review -> review.setRating(Math.round(((float) review.getRating()) / 10) * 10));
    }

    private static String getComment(ArtTeacher teacher, int rating) {
        return String.format("Commented by Teacher(%d, %s) and rated as %d.", teacher.getId(), teacher.getName(), rating);
    }
}