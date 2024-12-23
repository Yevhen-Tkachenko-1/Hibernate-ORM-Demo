package yevhent.demo.hibernate.entity.artschool;

import jakarta.persistence.*;
import lombok.*;
import yevhent.demo.hibernate.entity.Identifiable;

import java.util.List;

@Entity
@Table(schema = "art_school", name = "art_teachers")
@NoArgsConstructor // Required: used by the JPA provider to create instances of the entity using reflection
@AllArgsConstructor // Optional: used by this app
@Setter // Optional: used by this app
@Getter // Optional: used by this app
@EqualsAndHashCode
@ToString
@NamedQuery(
        name = "ArtTeacher.findTeachersWithReviewNumberMore",
        query = """      
                SELECT r.artTeacher.id AS id, r.artTeacher.name AS name, COUNT(r) AS number
                FROM ArtReview r
                WHERE r.artTeacher.id BETWEEN :teacherIdFrom AND :teacherIdTo
                GROUP BY r.artTeacher.id, r.artTeacher.name
                HAVING COUNT(r) > :minNumber
                """)
public class ArtTeacher implements Identifiable {

    public ArtTeacher(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ArtTeacher(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // when saving new Entity, ID is generated by DB
    @Column(name = "teacher_id")
    private int id;

    @Column(name = "teacher_name")
    private String name;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "artTeacher", cascade = CascadeType.ALL) // Back reference: specify our class the way it's declared in ArtReview class
    private List<ArtReview> artReviews;
}