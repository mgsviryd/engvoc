package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.URLToStringConverter;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.net.URL;

@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ExtractorErrorUrl implements Serializable {
    private static final long serialVersionUID = 1L;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(length = 500)
    @NonNull
    @Convert(converter = URLToStringConverter.class)
    private URL url;

    @Column(length = 500)
    @Length(max = 500)
    @NotEmpty
    private String message;

    @ManyToOne
    @JoinColumn(name = "extractor_error_id")
    private ExtractorError extractorError;

    public ExtractorErrorUrl(URL url, String message) {
        this.url = url;
        this.message = message;
    }
}
