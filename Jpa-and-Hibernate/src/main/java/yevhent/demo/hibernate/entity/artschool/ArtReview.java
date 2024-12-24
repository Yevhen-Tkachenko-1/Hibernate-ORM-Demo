package yevhent.demo.hibernate.entity.artschool;

import jakarta.persistence.*;
import lombok.*;
import yevhent.demo.hibernate.entity.general.Identifiable;

@Entity
@Table(schema = "art_school", name = "art_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NamedQuery(
        name = "ArtReview.roundReviewRatings",
        query = """
                UPDATE ArtReview r
                SET r.rating = ROUND(r.rating / 10) * 10
                WHERE r.artTeacher.id = :teacherId
                """)
@NamedQuery(
        name = "ArtReview.deleteReviewsWithRatingLower",
        query = """
                DELETE FROM ArtReview r
                WHERE r.artTeacher.id = :teacherId AND r.rating < :minRating
                """)
public class ArtReview implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int id;

    @Column(name = "review_comment")
    private String comment;

    @Column(name = "rating")
    private int rating;

    public ArtReview(String comment, int rating, ArtTeacher artTeacher) {
        this.comment = comment;
        this.rating = rating;
        this.artTeacher = artTeacher;
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "teacher_id") // Reflects FOREIGN KEY (teacher_id) REFERENCES art_school.art_teachers(teacher_id)
    private ArtTeacher artTeacher;

}
