package yevhent.demo.hibernate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "art_school", name = "art_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "artTeacher")
@ToString(onlyExplicitlyIncluded = true)
public class ArtReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    @ToString.Include
    private int id;

    @Column(name = "review_comment")
    @ToString.Include
    private String comment;

    @Column(name = "rating")
    @ToString.Include
    private int rating;

    @ManyToOne
    @JoinColumn(name = "teacher_id") // Reflects FOREIGN KEY (teacher_id) REFERENCES art_school.art_teachers(teacher_id)
    private ArtTeacher artTeacher;

}
