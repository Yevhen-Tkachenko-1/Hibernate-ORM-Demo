package yevhent.demo.hibernate.entity.general;

import jakarta.persistence.*;
import lombok.*;
import yevhent.demo.hibernate.entity.Identifiable;

@Entity
@Table(schema = "public", name = "versioned_items")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class VersionedItem implements Identifiable {

    public VersionedItem(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private int id;

    @Column(name = "item_name")
    private String name;

    @Version
    @Column(name = "version")
    private int version;

}