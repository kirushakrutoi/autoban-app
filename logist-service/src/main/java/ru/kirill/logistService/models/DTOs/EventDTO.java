package ru.kirill.logistService.models.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.kirill.logistService.models.Status;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private long id;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd ss:mm:HH")
    private LocalDateTime eventTime;
}
