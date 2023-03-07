package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(of = {"id", "text"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Message implements Serializable {
    public static final long serialVersionUID = 2L;

    public Message(String text, String tag, User author) {
        this.text = text;
        this.tag = tag;
        this.author = author;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Id.class)
    private Long id;

    @Column(length = 2048)
    @NotBlank(message = "Please fill the message.")
    @Length(max = 2048, message = "Message is too long. More than 2kB.")
    @JsonView(Views.Text.class)
    private String text;

    @Column(length = 255)
    @Length(max = 255, message = "Message is too long. More than 255B.")
    @JsonView(Views.Tag.class)
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    @Column(length = 255)
    private String filename;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh.mm.ss")
    @JsonView(Views.CreationLDT.class)
    private LocalDateTime creationLDT;
}
