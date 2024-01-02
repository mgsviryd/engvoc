package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString(of = {"id", "word", "translation", "unrepeated", "creationLDT"})
@EqualsAndHashCode(of = {"word", "translation", "unrepeated", "creationLDT"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
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
@JsonIgnoreProperties(value = {"cards"}, ignoreUnknown = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "word", "translation", "unrepeated", "creationLDT", "dictionary_id"}))
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    @DocumentId
    @JsonView(Views.Id.class)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonView(Views.User.class)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonView(Views.User.class)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id")
    @JsonView(Views.Vocabulary.class)
    private Vocabulary vocabulary;

    @Column(nullable = false, columnDefinition = "BIT", length = 1)
    @JsonView(Views.Unrepeated.class)
    private boolean unrepeated;

    @Column(length = 255)
    @Length(max = 255)
    @NotBlank
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.Word.class)
    private String word;

    @Column(length = 255)
    @Length(max = 255)
    @NotBlank
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.Translation.class)
    private String translation;

    @Column(length = 510)
    @Length(max = 510)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.Example.class)
    private String example;

    @Column(length = 510)
    @Length(max = 510)
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram1")),
            @Field(name = "name_ascii1", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii1"), store = Store.NO)
    })
    @JsonView(Views.ExampleTranslation.class)
    private String exampleTranslation;

    @Column(length = 255)
    @Length(max = 255)
    @JsonView(Views.Transcription.class)
    private String transcription;

    @Column(length = 50)
    @Length(max = 50)
    @JsonView(Views.Sound.class)
    private String sound;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.CreationLDT.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationLDT;

    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.LearnedLDT.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime learnedLDT;

    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.ForgotLDT.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime forgotLDT;


    @JsonView(Views.CountShown.class)
    private Integer countShown;

    @JsonView(Views.CountAnswered.class)
    private Integer countAnswered;

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

    @OneToOne
    @JsonView(Views.Dictionary.class)
    private Dictionary dictionary;

    public void makeLearned(){
        setLearned(true);
        setLearnedLDT(LocalDateTime.now());
        setCountShown(null);
    }
    public void makeUnlearned(){
        setLearned(false);
        setCountShown(null);
        setCountAnswered(null);
        setLearnedLDT(null);
    }
    public void makeForgot(){
        setCountForgot(getCountForgot()+1);
        setForgotLDT(LocalDateTime.now());
        makeUnlearned();
    }
}
