package by.sviryd.engvoc.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> content;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private int size;
    private int number;
    private List<SortOrderDTO> sort;

    public PageDTO(Page<T> page) {
        this.content = page.getContent();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.numberOfElements = page.getNumberOfElements();
        this.size = page.getSize();
        this.number = page.getNumber();

        Sort sort = page.getSort();
        this.sort = new ArrayList<>();
        for (Sort.Order order : sort) {
            this.sort.add(new SortOrderDTO(order));
        }
    }
}
