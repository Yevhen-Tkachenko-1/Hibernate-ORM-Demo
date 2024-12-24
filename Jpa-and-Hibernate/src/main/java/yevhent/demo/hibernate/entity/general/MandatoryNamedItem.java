package yevhent.demo.hibernate.entity.general;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yevhent.demo.hibernate.entity.general.Identifiable;

@Entity
@Table(schema = "public", name = "mandatory_named_items")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MandatoryNamedItem implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mandatory_named_item_id")
    private int id;

    @Basic(optional = false)
    @Column(name = "mandatory_named_item_name")
    private String name;
}