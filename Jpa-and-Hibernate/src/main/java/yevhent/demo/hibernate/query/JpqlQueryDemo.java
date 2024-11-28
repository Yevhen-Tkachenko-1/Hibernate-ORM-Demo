package yevhent.demo.hibernate.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import yevhent.demo.hibernate.configuration.ArtSchoolFactory;
import yevhent.demo.hibernate.entity.ArtClass;
import yevhent.demo.hibernate.entity.ArtReview;
import yevhent.demo.hibernate.entity.ArtStudent;

import java.util.List;

public class JpqlQueryDemo {

    public static void main(String[] args) {

        findAllStudents();
        findStudentClasses();
        findTeacherAverageReviewRating();
        findTeachersAverageReviewRatings();
        findTeachersAverageReviewRatingsGreaterThan40();
    }

    public static void findAllStudents() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT s FROM ArtStudent s";
            TypedQuery<ArtStudent> q1 = entityManager.createQuery(s1, ArtStudent.class);
            List<ArtStudent> artStudents = q1.getResultList();
            // Hibernate: select as1_0.student_id,as1_0.student_name from art_school.art_students as1_0
            // Select query to DB
            System.out.println("All Students: " + artStudents);
            // All Students: [ArtStudent(id=1, name=John), ArtStudent(id=2, name=Alice), ArtStudent(id=3, name=Bob), ArtStudent(id=4, name=Charlie), ArtStudent(id=5, name=Diana), ArtStudent(id=6, name=Eve), ArtStudent(id=7, name=Frank)]
        }
    }

    public static void findStudentClasses() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT s.artClasses FROM ArtStudent s WHERE s.name = :studentName";
            TypedQuery<ArtClass> q1 = entityManager.createQuery(s1, ArtClass.class)
                    .setParameter("studentName", "John");
            List<ArtClass> artClasses = q1.getResultList();
            // Hibernate: select ac1_1.class_id,ac1_1.teacher_id,ac1_1.class_name,ac1_1.week_day
            //            from art_school.art_students as1_0
            //            join art_school.students_classes_mapping ac1_0 on as1_0.student_id=ac1_0.student_id
            //            join art_school.art_classes ac1_1 on ac1_1.class_id=ac1_0.class_id
            //            where as1_0.student_name='John'
            // Select query to DB
            System.out.println("Student John attends " + artClasses);
            // Student John attends [ArtClass(id=5, name=Painting, weekDay=Monday), ArtClass(id=6, name=Music, weekDay=Saturday)]
        }
    }

    public static void findTeacherAverageReviewRating() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT AVG(r.rating) FROM ArtReview r WHERE r.artTeacher.name = :teacherName";
            TypedQuery<Double> q1 = entityManager.createQuery(s1, Double.class)
                    .setParameter("teacherName", "John");
            Double averageRating = q1.getSingleResult();
            // Hibernate: select avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            where at1_0.teacher_name='John'
            // Select query to DB
            System.out.println("Teacher John rated in average as " + averageRating);
            // Teacher John rated in average as 45.0
        }
    }

    public static void findTeachersAverageReviewRatings() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT t.name, AVG(r.rating) FROM ArtReview r JOIN r.artTeacher t GROUP BY t.name";
            TypedQuery<Object[]> q1 = entityManager.createQuery(s1, Object[].class);
            List<Object[]> averageRatings = q1.getResultList();
            // Hibernate: select at1_0.teacher_name,avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            group by at1_0.teacher_name
            // Select query to DB
            for (Object[] averageRating : averageRatings) {
                System.out.printf("Teacher %s rated in average as %s\n", averageRating[0], averageRating[1]);
            }
            // Teacher Mike rated in average as 45.0
            // Teacher Joe rated in average as 45.0
            // Teacher John rated in average as 45.0
        }
    }

    public static void findTeachersAverageReviewRatingsGreaterThan40() {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "SELECT t.name, AVG(r.rating) FROM ArtReview r JOIN r.artTeacher t GROUP BY t.name HAVING AVG(r.rating) > 40 ORDER BY AVG(r.rating) DESC";
            TypedQuery<Object[]> q1 = entityManager.createQuery(s1, Object[].class);
            List<Object[]> averageRatings = q1.getResultList();
            // Hibernate: select at1_0.teacher_name,avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            group by at1_0.teacher_name
            //            having avg(ar1_0.rating)>40
            //            order by avg(ar1_0.rating) desc
            // Select query to DB
            for (Object[] averageRating : averageRatings) {
                System.out.printf("Teacher %s rated in average as %s\n", averageRating[0], averageRating[1]);
            }
            // Teacher Mike rated in average as 45.0
            // Teacher Joe rated in average as 45.0
        }
    }

    public static void deleteReviewsRatingsLowerThan40(int teacherId) {
        try (EntityManagerFactory entityManagerFactory = ArtSchoolFactory.createEntityManagerFactory();
             EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String s1 = "DELETE FROM t.name, AVG(r.rating) FROM ArtReview r JOIN r.artTeacher t GROUP BY t.name HAVING AVG(r.rating) > 40 ORDER BY AVG(r.rating) DESC";
            TypedQuery<Object[]> q1 = entityManager.createQuery(s1, Object[].class);
            List<Object[]> averageRatings = q1.getResultList();
            // Hibernate: select at1_0.teacher_name,avg(ar1_0.rating)
            //            from art_school.art_reviews ar1_0
            //            join art_school.art_teachers at1_0 on at1_0.teacher_id=ar1_0.teacher_id
            //            group by at1_0.teacher_name
            //            having avg(ar1_0.rating)>40
            //            order by avg(ar1_0.rating) desc
            // Select query to DB
            for (Object[] averageRating : averageRatings) {
                System.out.printf("Teacher %s rated in average as %s\n", averageRating[0], averageRating[1]);
            }
            // Teacher Mike rated in average as 45.0
            // Teacher Joe rated in average as 45.0
        }
    }
}
