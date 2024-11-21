package yevhent.demo.hibernate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(schema = "art_school", name = "art_students")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class ArtStudent {

    public ArtStudent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    @ToString.Include
    private int id;

    @Column(name = "student_name")
    @ToString.Include
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(schema = "art_school", name = "students_classes_mapping",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id"))
    private List<ArtClass> artClasses;

}
