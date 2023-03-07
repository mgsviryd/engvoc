package by.sviryd.engvoc.domain.dto;

import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.type.EventType;
import by.sviryd.engvoc.type.ObjectType;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonView(Views.Id.class)
public class WsEventDTO {
    private ObjectType objectType;
    private EventType eventType;
    @JsonRawValue
    private String body;
}