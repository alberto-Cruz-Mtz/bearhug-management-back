package bearhug.management.app.persistence.entity;

import bearhug.management.app.persistence.model.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "rol")
public class RoleEntity {

    public RoleEntity(RoleType roleType) {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_de_rol")
    private RoleType roleType;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = PermissionEntity.class)
    @JoinTable(
            name = "rol_permiso",
            joinColumns =  @JoinColumn(name = "rol_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "permiso_id", nullable = false)
    )
    private Set<PermissionEntity> permissions;
}
