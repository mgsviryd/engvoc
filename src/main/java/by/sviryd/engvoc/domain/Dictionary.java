package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(of = {"id", "name", "unique"})
@EqualsAndHashCode(of = {"name", "unique", "creationLDT"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@JsonIgnoreProperties(value = {"cards"})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "unrepeated", "creationLDT"}))
public class Dictionary implements IIdParent, Serializable {
    public static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    @JsonView(Views.Id.class)
    private Long id;

    @Column(length = 100)
    @Length(max = 100)
    @NotBlank
    @NonNull
    @JsonView(Views.Name.class)
    private String name;

    @Min(0)
    private Long parent = 0L;

    @Column(name = "unrepeated", nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Unique.class)
    private boolean unique;

    @Column(length = 50)
    @Length(max = 50)
    @JsonView(Views.Picture.class)
    private String picture;

    @JsonView(Views.Priority.class)
    private Integer priority;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    private boolean invisible;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private User authorDictionary;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.CreationLDT.class)
    private LocalDateTime creationLDT;

    @JsonView(Views.ProductCount.class)
    @Formula("(select count(*) from Card p where p.dictionary_id = id)")
    private Long countCard;


    @OneToMany(mappedBy = "dictionary", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Card> cards = new ArrayList<>();

    public Dictionary(Long id) {
        this.id = id;
    }


    public Dictionary(String name) {
        this.name = name;
    }

    public Dictionary(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }
}
