package ru.kirill.logistService.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kirill.enums.Status;
import ru.kirill.logistService.models.DTOs.CompanyStatDTO;
import ru.kirill.logistService.models.DTOs.CreatePointDTO;
import ru.kirill.logistService.models.DTOs.EventDTO;
import ru.kirill.logistService.models.DTOs.RouteDTO;
import ru.kirill.logistService.models.Event;
import ru.kirill.logistService.models.Point;
import ru.kirill.logistService.models.Route;
import ru.kirill.logistService.models.Task;
import ru.kirill.logistService.services.RouteService;
import ru.kirill.logistService.services.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final TaskService taskService;


    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, TaskService taskService) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.taskService = taskService;
    }

    public void sendEventMessage(EventDTO eventDTO) throws JsonProcessingException {
        kafkaTemplate.send("event", objectMapper.writeValueAsString(eventDTO));
    }

    public void sendPointMessage(CreatePointDTO pointDTO) throws JsonProcessingException {
        kafkaTemplate.send("point", objectMapper.writeValueAsString(pointDTO));
    }

    public void sendStat() throws JsonProcessingException {
        List<Task> tasks = taskService.getAll();
        LocalDate now = LocalDate.now();
        Map<String, CompanyStatDTO> companiesStat = countStat(tasks, now);

        kafkaTemplate.send("stat", objectMapper.writeValueAsString(companiesStat.values()));
    }

    private Map<String, CompanyStatDTO> countStat(List<Task> tasks, LocalDate now){
        Map<String, CompanyStatDTO> companiesStat = new HashMap<>();

        for(Task task : tasks){
            if(task.getId() < 4)
                continue;

            String companyName = task.getCompanyName();

            if(!companiesStat.containsKey(companyName))
                addCompany(companiesStat, companyName);

            CompanyStatDTO companyStatDTO = companiesStat.get(companyName);
            LocalDate createTaskTime = task.getCreatedAt().toLocalDate();

            if(now.equals(createTaskTime))
                companyStatDTO.setCountTask(companyStatDTO.getCountTask() + 1);

            List<Route> routes = task.getRoutes();
            countRoutes(routes, companyStatDTO, now);
        }

        return companiesStat;
    }

    private void addCompany(Map<String, CompanyStatDTO> companiesStat, String companyName){
        CompanyStatDTO companyStatDTO = new CompanyStatDTO();
        companyStatDTO.setCompanyName(companyName);
        companiesStat.put(companyName, companyStatDTO);
    }

    private void countRoutes(List<Route> routes, CompanyStatDTO companyStatDTO, LocalDate now) {
        for(Route route : routes){
            LocalDate createRouteDate = route.getCreatedAt().toLocalDate();

            if(now.equals(createRouteDate)){
                List<Event> events = route.getEvents();
                for(Event event : events){
                    if(event.getStatus().equals(Status.STARTED))
                        companyStatDTO.setCountStartedRoute(companyStatDTO.getCountStartedRoute() + 1);
                    if(event.getStatus().equals(Status.CANCELLED))
                        companyStatDTO.setCountCanceledRoute(companyStatDTO.getCountCanceledRoute() + 1);
                    if(event.getStatus().equals(Status.ENDED))
                        companyStatDTO.setCountEndedRoute(companyStatDTO.getCountEndedRoute() + 1);
                }
            }
        }
    }
}
