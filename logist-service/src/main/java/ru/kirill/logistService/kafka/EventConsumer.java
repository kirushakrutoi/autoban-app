package ru.kirill.logistService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kirill.logistService.exceptions.ForbiddenException;
import ru.kirill.logistService.exceptions.IncorrectDataException;
import ru.kirill.logistService.exceptions.IncorrectStatusException;
import ru.kirill.logistService.exceptions.RouteNotFoundException;
import ru.kirill.logistService.models.DTOs.EventDTO;
import ru.kirill.logistService.services.EventService;

@Service
public class EventConsumer {
    private final EventService eventService;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventConsumer(EventService eventService, ObjectMapper objectMapper) {
        this.eventService = eventService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "event", groupId = "asdf")
    public void listen(ConsumerRecord<String, String> record) throws IncorrectDataException {


        try {
            EventDTO eventDTO = objectMapper.readValue(record.value(), EventDTO.class);
            eventService.create(eventDTO);
        } catch (RouteNotFoundException | IncorrectStatusException e) {
            System.out.println(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new IncorrectDataException("Incorrect data");
        }

    }
}
