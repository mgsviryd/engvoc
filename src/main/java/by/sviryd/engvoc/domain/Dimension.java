package by.sviryd.engvoc.domain;

import lombok.*;

import javax.persistence.Embeddable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Embeddable
public class Dimension {
    private Double length; // mm
    private Double width; // mm
    private Double height; // mm

    @Override
    public String toString() {
        return length + " x " + width + " x " + height;
    }
}
