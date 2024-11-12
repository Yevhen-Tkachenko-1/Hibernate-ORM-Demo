package yevhent.demo.hibernate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "art_school", name = "art_classes")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ArtClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private int id;

    @Column(name = "class_name")
    private String name;

    @Column(name = "week_day")
    private String weekDay;

}
