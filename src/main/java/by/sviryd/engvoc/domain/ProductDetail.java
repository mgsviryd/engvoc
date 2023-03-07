package by.sviryd.engvoc.domain;

import by.sviryd.engvoc.type.OrderStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ProductDetail implements Serializable {
    public static final long serialVersionUID = 1L;
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;
    private BigDecimal discount;
    private int count;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderDetail_id")
    private OrderDetail orderDetail;

    public double getAmount() {
        return price.subtract(discount).multiply(BigDecimal.valueOf(count)).setScale(SCALE, ROUNDING_MODE).doubleValue();
    }
}
