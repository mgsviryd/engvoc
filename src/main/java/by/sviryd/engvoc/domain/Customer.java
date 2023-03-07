package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User implements Serializable {
    public static final long serialVersionUID = 1L;
    @Embedded
    private Address address;

    @Column(length = 40)
    @ColumnDefault("''")
    @Length(max = 40)
    private String contactName;

    @Column(length = 40)
    @ColumnDefault("''")
    @Length(max = 40)
    private String contactTitle;

    @Column(length = 12)
    @Pattern(regexp = "375[0-9]{2}[0-9]{7}")
    private String contactPhone;

    @UpdateTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    private LocalDateTime lastModifiedLDT;
}
