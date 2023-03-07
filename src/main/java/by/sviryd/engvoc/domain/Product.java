package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import by.sviryd.engvoc.converter.URLToStringConverter;
import by.sviryd.engvoc.interceptor.ProductIndexingInterceptor;
import by.sviryd.engvoc.type.Unit;
import by.sviryd.engvoc.util.DoubleUtil;
import by.sviryd.engvoc.util.PriceUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ToString(of = {"id", "name", "code"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@NormalizerDef(
        name = "ascii",
        filters = {
                @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class), // To handle diacritics such as "é"
                @TokenFilterDef(factory = LowerCaseFilterFactory.class)
        }
)
@AnalyzerDef(name = "ngram",
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
@Indexed(interceptor = ProductIndexingInterceptor.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"code", "barcode"}))
@JsonIgnoreProperties(value = {"pictures", "descriptionPictures", "productDetails", "productProperties"})
public class Product implements INamePath, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int PLACES = 2;
    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    @JsonView(Views.Id.class)
    private Long id;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonView(Views.CreationLDT.class)
    private LocalDateTime creationLDT;

    @Column(length = 300)
    @Length(max = 300)
    @NotBlank
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram")),
            @Field(name = "name_ascii", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii"), store = Store.NO)
    })
    @JsonView(Views.Name.class)
    private String name;

    @Column(length = 500)
    @Length(max = 500)
    @JsonView(Views.Path.class)
    private String path;

    @Column(length = 500)
    @Convert(converter = URLToStringConverter.class)
    @JsonView(Views.Url.class)
    private URL url;

    @Column(length = 60)
    @Length(max = 60)
    @NotBlank
    @Fields({
            @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "ngram")),
            @Field(name = "code_ascii", analyze = Analyze.YES, normalizer = @Normalizer(definition = "ascii"), store = Store.NO),
    })

    @JsonView(Views.Code.class)
    private String code;

    @Column(length = 13)
    @Length(max = 13)
    @JsonView(Views.Barcode.class)
    private String barcode;

    @JsonView(Views.VendorCode.class)
    private String vendorCode;

    @Column(length = 5000)
    @Length(max = 5000)
    @Basic(fetch = FetchType.LAZY)
    @JsonView(Views.Description.class)
    private String description;

    @JsonView(Views.QuantityInStock.class)
    private Integer quantityInStock;

    @JsonView(Views.QuantitySupplier.class)
    private Integer quantitySupplier;

    @Basic(fetch = FetchType.LAZY)
    @JsonView(Views.QuantityReserved.class)
    private Integer quantityReserved;

    @Basic(fetch = FetchType.LAZY)
    @JsonView(Views.QuantityFuture.class)
    private Integer quantityFuture;

    @JsonView(Views.Price.class)
    private Double price;

    @JsonView(Views.Vat.class)
    private Double vat; // %

    @JsonView(Views.Discount.class)
    private Double discount;

    @JsonView(Views.Dimension.class)
    private Double length; // mm
    @JsonView(Views.Dimension.class)
    private Double width; // mm
    @JsonView(Views.Dimension.class)
    private Double height; // mm

    @JsonView(Views.DimensionPack.class)
    private Double lengthPack; // mm
    @JsonView(Views.DimensionPack.class)
    private Double widthPack; // mm
    @JsonView(Views.DimensionPack.class)
    private Double heightPack; // mm

    @JsonView(Views.DimensionCarry.class)
    private Double lengthCarry; // mm
    @JsonView(Views.DimensionCarry.class)
    private Double widthCarry; // mm
    @JsonView(Views.DimensionCarry.class)
    private Double heightCarry; // mm

    @JsonView(Views.Weight.class)
    private Double weight; // kg

    @JsonView(Views.WeightPack.class)
    private Double weightPack; // kg

    @JsonView(Views.WeightCarry.class)
    private Double weightCarry; // kg

    @Column(length = 20)
    @Length(max = 20)
    @Basic(fetch = FetchType.LAZY)
    @JsonView(Views.DeliveryCountry.class)
    private String deliveryCountry;

    @Column(length = 20)
    @Length(max = 20)
    @JsonView(Views.MadeCountry.class)
    private String madeCountry;

    @Column(length = 50)
    @Length(max = 50)
    @JsonView(Views.Picture.class)
    private String picture;


    @Enumerated(EnumType.STRING)
    @JsonView(Views.Unit.class)
    private Unit unit = Unit.ITEM;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.UnitPack.class)
    private Unit unitPack = Unit.ITEM;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.UnitCarry.class)
    private Unit unitCarry = Unit.ITEM;

    @JsonView(Views.Count.class)
    private Integer count;
    @JsonView(Views.CountPack.class)
    private Integer countPack;
    @JsonView(Views.CountCarry.class)
    private Integer countCarry;

    @Field
    @SortableField
    @JsonView(Views.Popular.class)
    private boolean popular;

    @JsonView(Views.Visitors.class)
    private Long visitors;

    @JsonView(Views.Fresh.class)
    private boolean fresh;
    @JsonView(Views.Invisible.class)
    private boolean invisible;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Supplier.class)
    private Supplier supplier;

    @Column(length = 60)
    @Length(max = 60)
    @JsonView(Views.VendorShort.class)
    private String vendorShort;

    @Column(length = 255)
    @Length(max = 255)
    @JsonView(Views.VendorFull.class)
    private String vendorFull;

    @Column(length = 50)
    @Length(max = 50)
    @JsonView(Views.VendorPicture.class)
    private String vendorPicture;

    @Column(length = 255)
    @Length(max = 255)
    @JsonView(Views.ServiceCenter.class)
    private String serviceCenter;

    @OneToOne
    @IndexedEmbedded(includePaths = {"name"})
    @JsonView(Views.Category.class)
    private Category category;

    @OneToMany(mappedBy = "productPicture")
    List<Picture> pictures = new ArrayList<>();

    @OneToMany(mappedBy = "productDescriptionPicture")
    List<Picture> descriptionPictures = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private Set<ProductDetail> productDetails = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<ProductProperty> productProperties = new HashSet<>();

    @Transient
    @JsonView(Views.PriceWithDiscount.class)
    private Double priceWithDiscount;

    @Transient
    @JsonView(Views.PriceWithoutVatWithDiscount.class)
    private Double priceWithoutVatWithDiscount;

    @PostLoad
    private void postLoad() {
        if (price != null) {
            if (discount != null) {
                priceWithDiscount = DoubleUtil.round(PriceUtil.getRowPriceWithDiscount(price, discount), PLACES, ROUNDING_MODE);
            } else {
                priceWithDiscount = price;
            }
            if (vat != null && discount != null) {
                double priceWithoutVat = DoubleUtil.round(PriceUtil.getRowPriceWithoutVat(price, vat), PLACES, ROUNDING_MODE);
                priceWithoutVatWithDiscount = DoubleUtil.round(PriceUtil.getRowPriceWithDiscount(priceWithoutVat, discount), PLACES, ROUNDING_MODE);
            } else if (vat != null) {
                priceWithoutVatWithDiscount = DoubleUtil.round(PriceUtil.getRowPriceWithoutVat(price, vat), PLACES, ROUNDING_MODE);
            } else {
                priceWithoutVatWithDiscount = price;
            }
        }
    }

    @Transient
    public Double getPriceWithDiscount() {
        return priceWithDiscount;
    }
    @Transient
    public Double getPriceWithoutVatWithDiscount() {
        return priceWithoutVatWithDiscount;
    }

    public void setDimension(Dimension dimension){
        this.length = dimension.getLength();
        this.width = dimension.getWidth();
        this.height = dimension.getHeight();
    }
    public void setDimensionPack(Dimension dimension){
        this.lengthPack = dimension.getLength();
        this.widthPack = dimension.getWidth();
        this.heightPack = dimension.getHeight();
    }
    public void setDimensionCarry(Dimension dimension){
        this.lengthCarry = dimension.getLength();
        this.widthCarry = dimension.getWidth();
        this.heightCarry = dimension.getHeight();
    }
}
