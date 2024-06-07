package ru.kirill.logistService.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import ru.kirill.logistService.exceptions.IncorrectStatusException;
import ru.kirill.logistService.models.*;
import ru.kirill.logistService.models.DTOs.*;

import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

public class Mapper {
    public static Task convertToTask(TaskDTO taskDTO){
        Task task = new Task();

        task.setCompanyName(taskDTO.getCompanyName());
        task.setEndPoint(taskDTO.getEndPoint());
        task.setStartPoint(taskDTO.getStartPoint());
        task.setDriverFirstName(taskDTO.getDriverDTO().getFirstName());
        task.setDriverLastName(taskDTO.getDriverDTO().getLastName());
        task.setOrderDescription(taskDTO.getOrderDescription());
        task.setStateNumber(taskDTO.getCarDTO().getStateNumber());

        return task;
    }

    public static TaskDTO convertToTaskDTO(CreateTaskDTO createTaskDTO, DriverDTO driverDTO, CarDTO carDTO){
        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setCompanyName(createTaskDTO.getCompanyName());
        taskDTO.setEndPoint(createTaskDTO.getEndPoint());
        taskDTO.setStartPoint(createTaskDTO.getStartPoint());
        taskDTO.setDriverDTO(driverDTO);
        taskDTO.setOrderDescription(createTaskDTO.getOrderDescription());
        taskDTO.setCarDTO(carDTO);

        return taskDTO;
    }

    public static TaskDTO convertToTaskDTO(Task task){
        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setCompanyName(task.getCompanyName());
        taskDTO.setEndPoint(task.getEndPoint());
        taskDTO.setStartPoint(task.getStartPoint());
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setFirstName(task.getDriverFirstName());
        driverDTO.setLastName(task.getDriverLastName());
        taskDTO.setDriverDTO(driverDTO);
        taskDTO.setOrderDescription(task.getOrderDescription());
        taskDTO.setCarDTO(new CarDTO(task.getStateNumber()));

        return taskDTO;
    }

    public static RouteDTO convertToRouteDTO(Route route){
        RouteDTO routeDTO = new RouteDTO();

        routeDTO.setCreatedAt(route.getCreatedAt());
        routeDTO.setStartTime(route.getStartTime());
        routeDTO.setEndTime(route.getEndTime());
        routeDTO.setEvents(route.getEvents().stream().map(Mapper::convertToEventDTO).collect(Collectors.toList()));
        routeDTO.setLocations(route.getLocations().stream().map(Mapper::cinvertToPointDTO).collect(Collectors.toList()));

        return routeDTO;
    }

    public static EventDTO convertToEventDTO(Event event){
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventTime(event.getCreatedAt());
        eventDTO.setStatus(String.valueOf(event.getStatus()));
        eventDTO.setId(event.getId());
        return eventDTO;
    }

    public static PointDTO cinvertToPointDTO(Point point){
        PointDTO pointDTO = new PointDTO();

        pointDTO.setLat(point.getLatitude());
        pointDTO.setLon(point.getLongitude());

        return pointDTO;
    }

    public static Event convertToEvent(EventDTO eventDTO) throws IncorrectStatusException {
        Event event = new Event();
        try {
            event.setStatus(Status.getStatByString(eventDTO.getStatus()));
        } catch (IllegalArgumentException e){
            throw new IncorrectStatusException("Incorrect status name");
        }

        event.setCreatedAt(eventDTO.getEventTime());
        return event;
    }

    public static Point convertToPoint(CreatePointDTO pointDTO) {
        Point point = new Point();

        point.setLatitude(pointDTO.getLat());
        point.setLongitude(pointDTO.getLon());

        return point;
    }

    public static User getUserFromHeaders(HttpHeaders headers) throws JsonProcessingException {
        User user = new User();
        user.setUserId(headers.get("userid").get(0));
        user.setUsername(headers.get("username").get(0));
        user.setClientRoles(getClientRoles(headers));
        return user;
    }

    private static Map<String, String> getClientRoles(HttpHeaders headers) throws JsonProcessingException {
        String clientRolesHeader = headers.get("clientroles").get(0);
        String encodeHeader = new String(Base64.getDecoder().decode(clientRolesHeader));
        return new ObjectMapper().readerFor(Map.class).readValue(encodeHeader);
    }
}
