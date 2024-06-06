package ru.kirill.logistService.models.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.Data;
import ru.kirill.logistService.models.Event;
import ru.kirill.logistService.models.Point;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RouteDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd ss:mm:HH")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime endTime;
    private List<EventDTO> events;
    private List<PointDTO> locations;
}
