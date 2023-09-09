package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(of = {"user"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table
public class VerificationToken implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 16)
    @NotNull
    @Length(max = 16)
    private String token = UUID.randomUUID().toString().substring(0,16);

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    private LocalDateTime expiryDate;

    public boolean isExpiredToken(){
        return expiryDate.isBefore(LocalDateTime.now());
    }
}