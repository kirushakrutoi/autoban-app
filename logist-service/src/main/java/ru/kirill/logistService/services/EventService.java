package ru.kirill.logistService.services;

import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.enums.Status;
import ru.kirill.logistService.exceptions.ForbiddenException;
import ru.kirill.logistService.exceptions.IncorrectStatusException;
import ru.kirill.logistService.exceptions.RouteNotFoundException;
import ru.kirill.logistService.mappers.Mapper;
import ru.kirill.logistService.models.DTOs.EventDTO;
import ru.kirill.logistService.models.Event;
import ru.kirill.logistService.models.Route;
import ru.kirill.logistService.repositories.EventRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RouteService routeService;

    @Transactional
    public void create(EventDTO eventDTO) throws RouteNotFoundException, IncorrectStatusException {
        Event event = Mapper.convertToEvent(eventDTO);
        Route route = routeService.get(eventDTO.getId());
        event.setRoute(route);
        eventRepository.save(event);

        if(event.getStatus().equals(Status.STARTED)){
            route.setStartTime(event.getCreatedAt());
            routeService.update(route);

        } else if (event.getStatus().equals(Status.ENDED)) {
            route.setEndTime(event.getCreatedAt());
            routeService.update(route);
        }
    }
}
