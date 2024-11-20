package yevhent.demo.hibernate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "art_school", name = "art_students")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ArtStudent {

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(schema = "art_school", name = "students_classes_mapping",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id"))
    private List<ArtClass> artClasses;

}
