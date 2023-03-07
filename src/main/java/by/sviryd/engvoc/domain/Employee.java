package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateToTimestampConverter;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(name = "person_id")
public class Employee extends Person implements Serializable {
    public static final long serialVersionUID = 1L;
    @Length(max = 20)
    @NotBlank
    private String title;

    @CreationTimestamp
    @Convert(converter = LocalDateToTimestampConverter.class)
    private LocalDate hireLD;

    @NotNull
    @Convert(converter = LocalDateToTimestampConverter.class)
    private LocalDate expireContractLD;

    @CreationTimestamp
    @Convert(converter = LocalDateToTimestampConverter.class)
    private LocalDate birthLD;

    @Lob
    private byte[] photo;

    @Min(0)
    private int salary;

    @Singular
    @OneToMany(mappedBy = "employee", cascade = CascadeType.PERSIST)
    private Set<Order> orders = new HashSet<>();
}
