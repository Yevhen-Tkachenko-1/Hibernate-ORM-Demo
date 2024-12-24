package yevhent.demo.hibernate.entity.artschool;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "art_school", name = "art_classes")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class ArtClass {

    public ArtClass(int id, String name, String weekDay) {
        this.id = id;
        this.name = name;
        this.weekDay = weekDay;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    @ToString.Include
    private int id;

    @Column(name = "class_name")
    @ToString.Include
    private String name;

    @Column(name = "week_day")
    @ToString.Include
    private String weekDay;

    @OneToOne
    @JoinColumn(name = "teacher_id") // Reflects FOREIGN KEY (teacher_id) REFERENCES art_teachers(teacher_id)
    private ArtTeacher artTeacher;
}
