package yevhent.demo.hibernate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "art_school", name = "art_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ArtReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int id;

    @Column(name = "review_comment")
    private String comment;

    @Column(name = "rating")
    private int rating;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "teacher_id") // Reflects FOREIGN KEY (teacher_id) REFERENCES art_school.art_teachers(teacher_id)
    private ArtTeacher artTeacher;

}
