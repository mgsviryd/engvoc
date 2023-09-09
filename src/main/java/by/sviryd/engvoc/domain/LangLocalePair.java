package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.type.LangLocale;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.thymeleaf.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class LangLocalePair implements Serializable {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Length(max = 10)
    @NotBlank
    @NonNull
    @Column(name="pair_source", length = 10)
    @JsonView(Views.LangLocale.class)
    private LangLocale source;

    @Enumerated(EnumType.STRING)
    @Length(max = 10)
    @NotBlank
    @NonNull
    @Column(name = "pair_target", length = 10)
    @JsonView(Views.LangLocale.class)
    private LangLocale target;

//    @Column(name = "pair_active", nullable = false, columnDefinition = "BIT", length = 1)
//    @JsonView(Views.Active.class)
//    private boolean active;

    @JsonIgnore
    public String getCapitalizeLangPair() {
        return StringUtils.capitalize(source.getLang()) + StringUtils.capitalize(target.getLang());
    }
}
