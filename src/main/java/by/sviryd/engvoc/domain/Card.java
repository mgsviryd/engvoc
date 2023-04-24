package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(of = {"id", "word", "translation", "unique", "creationLDT"})
@EqualsAndHashCode(of = {"word", "translation", "unique", "creationLDT"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@NormalizerDef(
        name = "ascii1",
        filters = {
                @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class), // To handle diacritics such as "é"
                @TokenFilterDef(factory = LowerCaseFilterFactory.class)
        }
)
@AnalyzerDef(name = "ngram1",
        tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = StandardFilterFactory.class),
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
//                @TokenFilterDef(factory = UpperCaseFilterFactory.class),
//                @TokenFilterDef(factory = SnowballPorterFilterFactory.class),
                @TokenFilterDef(factory = StopFilterFactory.class),
                @TokenFilterDef(factory = NGramFilterFactory.class,
                        params = {
                                @Parameter(name = "minGramSize", value = "3"),
                                @Parameter(name = "maxGramSize", value = "5")
                        })
        }
)
@Table
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    @JsonView(Views.Id.class)
    private Long id;

    @Column(name = "unrepeated", nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Unique.class)
    private boolean unique;


    @Column(length = 100)
    @Length(max = 100)
    @NotBlank
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.Word.class)
    private String word;

    @Column(length = 100)
    @Length(max = 100)
    @NotBlank
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.Translation.class)
    private String translation;

    @Column(length = 500)
    @Length(max = 500)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.Example.class)
    private String example;

    @Column(length = 500)
    @Length(max = 500)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.ExampleTranslation.class)
    private String exampleTranslation;

    @Column(length = 100)
    @Length(max = 100)
    @JsonView(Views.Transcription.class)
    private String transcription;

    @Column(length = 500)
    @Length(max = 500)
    @JsonView(Views.Sound.class)
    private String sound;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.CreationLDT.class)
    private LocalDateTime creationLDT;

    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.LearnedLDT.class)
    private LocalDateTime learnedLDT;

    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.ForgotLDT.class)
    private LocalDateTime forgotLDT;

    @JsonView(Views.CountForgot.class)
    private Integer countForgot;

    @Column(length = 50)
    @Length(max = 50)
    @JsonView(Views.Picture.class)
    private String picture;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Invisible.class)
    private boolean invisible;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Learned.class)
    private boolean learned;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id")
//    private User authorCard;

    @OneToOne
    @JsonView(Views.Dictionary.class)
    private Dictionary dictionary;
}
