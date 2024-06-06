package ru.kirill.logistService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kirill.logistService.exceptions.ForbiddenException;
import ru.kirill.logistService.exceptions.IncorrectDataException;
import ru.kirill.logistService.exceptions.RouteNotFoundException;
import ru.kirill.logistService.models.DTOs.CreatePointDTO;
import ru.kirill.logistService.models.DTOs.CreateRouteDTO;
import ru.kirill.logistService.models.DTOs.EventDTO;
import ru.kirill.logistService.services.EventService;
import ru.kirill.logistService.services.PointService;

@Service
public class LocationPointConsumer {
    @Autowired
    private PointService pointService;
    @Autowired
    private ObjectMapper objectMapper;
    @KafkaListener(topics = "point", groupId = "asdf")
    public void listen(ConsumerRecord<String, String> record) throws IncorrectDataException {
        try {
            CreatePointDTO pointDTO = objectMapper.readValue(record.value(), CreatePointDTO.class);
            pointService.create(pointDTO);
        } catch (RouteNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new IncorrectDataException("Incorrect data");
        }

    }
}
