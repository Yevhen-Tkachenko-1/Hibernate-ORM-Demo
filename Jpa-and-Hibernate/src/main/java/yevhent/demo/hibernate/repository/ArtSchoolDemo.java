package yevhent.demo.hibernate.repository;

import jakarta.persistence.EntityManagerFactory;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.artschool.ArtReview;
import yevhent.demo.hibernate.entity.artschool.ArtTeacher;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArtSchoolDemo {

    public static void main(String[] args) {

        ArtTeacherRepository teacherRepository = new ArtTeacherRepositoryImpl();
        EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();

        ArtSchoolService service = new ArtSchoolService(teacherRepository, entityManagerFactory);

        Map<String, List<Integer>> teacherRequest = Stream.of("Mr. Smith", "Ms. Johnson", "Dr. Miller", "Prof. Davis", "Mrs. Williams")
                .collect(Collectors.toMap(Function.identity(), name-> getRandomRatings()));

        List<Integer> persistedTeacherIds = service.saveTeachersWithReviews(teacherRequest);
        List<ArtTeacher> persistedTeachers = service.getTeachersWithReviews(persistedTeacherIds);
        for (ArtTeacher teacher : persistedTeachers) {
            System.out.println("Persisted:");
            System.out.println(teacher);
            for (ArtReview review : teacher.getArtReviews()) {
                System.out.println(review);
            }
        }
        List<Integer> processedTeacherIds = service.processTeachersReviews(persistedTeacherIds, 6, 30);
        List<ArtTeacher> processedTeachers = service.getTeachersWithReviews(processedTeacherIds);
        for (ArtTeacher teacher : processedTeachers) {
            System.out.println("Processed:");
            System.out.println(teacher);
            for (ArtReview review : teacher.getArtReviews()) {
                System.out.println(review);
            }
        }
    }

    private static List<Integer> getRandomRatings() {
        int total = ThreadLocalRandom.current().nextInt(3, 11);
        return new Random().ints(total, 1, 100).boxed().collect(Collectors.toList());
    }
}
