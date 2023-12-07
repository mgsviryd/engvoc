package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.type.LangLocale;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.validator.constraints.Length;
import org.thymeleaf.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString(of = {"id", "name", "source", "target"})
@EqualsAndHashCode(of = {"name", "source", "target"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(value = {"author", "dictionaries", "cards"})
@Entity
@Table
public class Vocabulary implements Serializable {
    private static final long serialVersionUID = 1L;

    @DocumentId
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @JsonView(Views.Id.class)
    private UUID id;

    @Length(max = 100)
    @NotBlank
    @NonNull
    @Column(length = 100)
    @JsonView(Views.Name.class)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @JsonView(Views.Source.class)
    private LangLocale source;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @JsonView(Views.Target.class)
    private LangLocale target;

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dictionary> dictionaries = new ArrayList<>();

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Card> cards = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @JsonIgnore
    public String getCapitalizeLangPair() {
        return StringUtils.capitalize(source.getLang()) + StringUtils.capitalize(target.getLang());
    }
}
