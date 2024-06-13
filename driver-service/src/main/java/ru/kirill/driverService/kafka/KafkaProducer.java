package ru.kirill.driverService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kirill.enums.Status;
import ru.kirill.exceptions.StatusNotExistException;
import ru.kirill.models.DTOs.CreatePointDTO;
import ru.kirill.models.DTOs.EventDTO;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendEventMessage(EventDTO eventDTO) throws JsonProcessingException, StatusNotExistException {
        Status.getStatByString(eventDTO.getStatus());
        kafkaTemplate.send("event", objectMapper.writeValueAsString(eventDTO));
    }

    public void sendPointMessage(CreatePointDTO pointDTO) throws JsonProcessingException {
        kafkaTemplate.send("point", objectMapper.writeValueAsString(pointDTO));
    }
}
