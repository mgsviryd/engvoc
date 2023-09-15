package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString(of = {"id", "name", "unrepeated", "creationLDT"})
@EqualsAndHashCode(of = {"name", "unrepeated", "creationLDT"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(value = {"user", "cards"})
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"author_id", "name", "unrepeated", "creationLDT"}))
public class Dictionary implements Serializable {
    private static final long serialVersionUID = 1L;

    @DocumentId
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @JsonView(Views.Id.class)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonView(Views.User.class)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id")
    @JsonView(Views.Vocabulary.class)
    private Vocabulary vocabulary;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Unrepeated.class)
    private boolean unrepeated;

    @Length(max = 100)
    @NotBlank
    @NonNull
    @Column(length = 100)
    @JsonView(Views.Name.class)
    private String name;

    @Min(0)
    private Long parent = 0L;

    @Column(length = 50)
    @Length(max = 50)
    @JsonView(Views.Picture.class)
    private String picture;

    @JsonView(Views.Priority.class)
    private Integer priority;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    private boolean invisible;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.CreationLDT.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationLDT = LocalDateTime.now();

    @JsonView(Views.CountCard.class)
    @Formula("(select count(*) from Card p where p.dictionary_id = id)")
    private Long countCard;


    @OneToMany(mappedBy = "dictionary", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Card> cards = new ArrayList<>();

    public Dictionary(UUID id) {
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
