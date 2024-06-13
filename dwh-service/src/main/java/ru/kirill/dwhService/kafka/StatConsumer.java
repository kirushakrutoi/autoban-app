package ru.kirill.dwhService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.dwhService.mapper.Mapper;
import ru.kirill.dwhService.models.CompanyStat;
import ru.kirill.dwhService.models.DTOs.CompanyStatDTO;
import ru.kirill.dwhService.services.StatService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatConsumer {

    private final StatService statService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StatConsumer(StatService statService, ObjectMapper objectMapper) {
        this.statService = statService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @KafkaListener(topics = "stat", groupId = "asdf")
    public void listen(ConsumerRecord<String, String> record) throws JsonProcessingException {
        List<CompanyStatDTO> companyStatDTOs = objectMapper.readValue(record.value(), new TypeReference<List<CompanyStatDTO>>() {});

        List<CompanyStat> companyStats = companyStatDTOs.stream().map(Mapper::convertToCompanyStat).collect(Collectors.toList());

        for(CompanyStat companyStat : companyStats){
            statService.saveStat(companyStat);
        }
    }
}
