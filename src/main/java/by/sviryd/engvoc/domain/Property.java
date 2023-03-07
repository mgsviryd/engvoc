package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.type.PropertyType;
import by.sviryd.engvoc.type.Unit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ToString(of = {"id", "category", "name"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "name"}))
@JsonIgnoreProperties(value = {"category"})
public class Property implements INamePath, Serializable {
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 100)
    @Length(max = 100)
    @NotBlank
    private String name;

    @Column(length = 100)
    @Length(max = 100)
    private String path;

    @Column(length = 255)
    @Length(max = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    private PropertyType type = PropertyType.STRING;

    @Enumerated(EnumType.STRING)
    private Unit unit = Unit.UNDEFINED;

    private Integer priority;

    private boolean invisible;
    private boolean supplement;

    public Property( @Length(max = 100)
                     @NotBlank String name) {
        this.name = name;
    }

    public Property(@Length(max = 100) @NotBlank String name,
                    @Length(max = 100) String path,
                    PropertyType type,
                    Unit unit) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.unit = unit;
    }
}
