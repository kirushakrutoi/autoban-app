package ru.kirill.logistService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kirill.logistService.models.DTOs.CreatePointDTO;
import ru.kirill.logistService.models.DTOs.EventDTO;
import ru.kirill.logistService.models.DTOs.RouteDTO;
import ru.kirill.logistService.models.Point;
import ru.kirill.logistService.models.Route;

@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void sendEventMessage(EventDTO eventDTO) throws JsonProcessingException {
        kafkaTemplate.send("event", objectMapper.writeValueAsString(eventDTO));
    }

    public void sendPointMessage(CreatePointDTO pointDTO) throws JsonProcessingException {
        kafkaTemplate.send("point", objectMapper.writeValueAsString(pointDTO));
    }
}
