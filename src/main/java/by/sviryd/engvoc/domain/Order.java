package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.converter.LocalDateTimeToTimestampConverter;
import by.sviryd.engvoc.type.OrderStatus;
import by.sviryd.engvoc.type.ShipVia;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer_order")
public class Order implements Serializable {
    public static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 40)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne
    private OrderDetail orderDetail;

    @CreationTimestamp
    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    private LocalDateTime createLDT = LocalDateTime.now();

    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    private LocalDateTime requiredLDT;

    @Convert(converter = LocalDateTimeToTimestampConverter.class)
    private LocalDateTime shippedLDT;

    @Column(length = 40)
    @Enumerated(EnumType.STRING)
    private ShipVia shipVia;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freight_id")
    private Freight freight;

    @Embedded
    private Address shipAddress;
}
