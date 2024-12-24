package yevhent.demo.hibernate.entity.artschool;

import jakarta.persistence.*;
import lombok.*;
import yevhent.demo.hibernate.entity.Identifiable;

import java.util.List;

@Entity
@Table(schema = "art_school", name = "art_students")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ArtStudent implements Identifiable {

    public ArtStudent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private int id;

    @Column(name = "student_name")
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(schema = "art_school", name = "students_classes_mapping",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id"))
    private List<ArtClass> artClasses;

}
