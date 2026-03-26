package fit.hutech.spring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    @Size(min = 1, max = 100, message = "Warehouse name must be between 1 and 100 characters")
    @NotBlank(message = "Warehouse name must not be blank")
    private String name;

    @Column(name = "address", length = 255)
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Warehouse warehouse = (Warehouse) o;
        return getId() != null && Objects.equals(getId(), warehouse.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
