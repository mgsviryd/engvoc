package by.sviryd.engvoc.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;
import lombok.Value;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@ToString
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class SortOrderDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private String property;
    private String direction;
    private boolean ignoreCase;
    private String nullHandling;

    public SortOrderDTO(Sort.Order order){
        this.property = order.getProperty();
        this.direction = order.getDirection().name();
        this.ignoreCase = order.isIgnoreCase();
        this.nullHandling = order.getNullHandling().name();
    }
}
