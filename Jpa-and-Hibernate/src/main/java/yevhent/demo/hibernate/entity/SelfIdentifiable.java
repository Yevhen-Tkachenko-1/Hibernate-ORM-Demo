package yevhent.demo.hibernate.entity;

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
}
