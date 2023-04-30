package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.URLToStringConverter;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URL;

@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(length = 255)
    @Length(max = 255)
    @NotEmpty
    private String path;

    @Column(length = 500)
    @NonNull
    @Convert(converter = URLToStringConverter.class)
    private URL sourceUrl;

    @NotNull
    private Integer priority;


    public Picture(String path, URL sourceUrl, Integer priority) {
        this.path = path;
        this.sourceUrl = sourceUrl;
        this.priority = priority;
    }
}
