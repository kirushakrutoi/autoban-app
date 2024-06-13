package ru.kirill.logistService.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.kirill.logistService.kafka.KafkaProducer;

@Component
public class SendStatService {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Scheduled(cron = "0 * * * * *")
    public void executeHourlyTask() throws JsonProcessingException {
        kafkaProducer.sendStat();
    }
}
