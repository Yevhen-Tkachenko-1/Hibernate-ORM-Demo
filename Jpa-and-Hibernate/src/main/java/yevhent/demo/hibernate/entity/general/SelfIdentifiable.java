package yevhent.demo.hibernate.entity.general;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "public", name = "self_assigned_ids")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SelfIdentifiable {

    @Id
    @Column(name = "identity_id")
    private int id;

    // method add 2 numbers
}
