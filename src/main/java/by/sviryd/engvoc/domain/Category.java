package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.interceptor.CategoryIndexingInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ToString(of = {"name", "parent"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Indexed(interceptor = CategoryIndexingInterceptor.class)
@JsonIgnoreProperties(value = {"products", "properties"})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "parent"}))
public class Category implements IIdParent, INamePath, Serializable {
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
    @Fields({
            @Field(index= Index.YES, analyze= Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram")),
            @Field(name = "name_ascii", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii"), store = Store.NO)
    })
    @JsonView(Views.Name.class)
    private String name;

    @Min(0)
    private Long parent;

    @Column(length = 255)
    @Length(max = 255)
    @JsonView(Views.Path.class)
    private String path;

    @Column(length = 150)
    @Length(max = 150)
    @JsonView(Views.Picture.class)
    private String picture;

    @JsonView(Views.Priority.class)
    private Integer priority;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    private boolean invisible;

    @JsonView(Views.ProductCount.class)
    @Formula("(select count(*) from Product p where p.category_id = id and p.invisible = 0)")
    private Long productCount;

    @OneToMany(mappedBy = "category")
    @IndexedEmbedded(includePaths = { "name", "code"})
    private List<Product> products = new ArrayList<>();
    @OneToMany(mappedBy = "category")
    private List<Property> properties = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }
    public Category(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }
}
