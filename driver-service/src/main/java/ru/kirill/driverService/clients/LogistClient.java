package ru.kirill.driverService.clients;

import feign.HeaderMap;
import feign.RequestLine;
import ru.kirill.models.DTOs.CreateRouteDTO;
import ru.kirill.models.DTOs.TaskDTO;

import java.util.List;
import java.util.Map;

public interface LogistClient {
    @RequestLine("GET /task/driver/get")
    List<TaskDTO> findDriverTask(@HeaderMap Map<String, String> headers);

    @RequestLine("POST /route")
    void createRoute(@HeaderMap Map<String, String> headers,
                     CreateRouteDTO createRouteDTO);
}
