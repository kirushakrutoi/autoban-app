package ru.kirill.logistService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.enums.Status;
import ru.kirill.logistService.exceptions.ForbiddenException;
import ru.kirill.logistService.exceptions.IncorrectDataException;
import ru.kirill.logistService.exceptions.RouteNotFoundException;
import ru.kirill.logistService.exceptions.TaskNotFoundException;
import ru.kirill.logistService.mappers.Mapper;
import ru.kirill.logistService.models.*;
import ru.kirill.logistService.models.DTOs.CreateRouteDTO;
import ru.kirill.logistService.models.DTOs.EventDTO;
import ru.kirill.logistService.repositories.EventRepository;
import ru.kirill.logistService.repositories.RouteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TaskService taskService;

    @Transactional(readOnly = true)
    public Route get(long id) throws RouteNotFoundException {
        Optional<Route> ORoute = routeRepository.findById(id);

        if(!ORoute.isPresent())
            throw new RouteNotFoundException("Route with this id not found");

        return ORoute.get();
    }

    @Transactional(readOnly = true)
    public Route get(long id, User user) throws ForbiddenException, RouteNotFoundException {
        Optional<Route> ORoute = routeRepository.findById(id);

        if(!ORoute.isPresent())
            throw new RouteNotFoundException("Route with this id not found");

        Route route = ORoute.get();
        Task task = route.getTask();

        if(!checkAuthority(user, task.getCompanyName(), "LOGIST"))
            throw new ForbiddenException("You are not a LOGIST of this company");

        return route;
    }

    @Transactional(readOnly = true)
    public List<Route> getAll(Integer page, Integer pageSize, long taskId, User user) throws IncorrectDataException, ForbiddenException, TaskNotFoundException {
        Task task = taskService.get(taskId, user);

        if(!checkAuthority(user, task.getCompanyName(), "LOGIST"))
            throw new ForbiddenException("You are not a LOGIST of this company");

        if(page != null && pageSize != null) {
            List<Route> routes = task.getRoutes();
            int size = routes.size();
            int fromIndex = page * pageSize;
            int toIndex = fromIndex + pageSize;

            if(toIndex >= size)
                return Optional.of(task.getRoutes().subList(fromIndex, size - 1)).orElse(new ArrayList<>());
            return Optional.of(task.getRoutes().subList(fromIndex, toIndex - 1)).orElse(new ArrayList<>());
        }

        if(page != null || pageSize != null)
            throw new IncorrectDataException("Page or page size can't be empty");

        return task.getRoutes();
    }

    private boolean checkAuthority(User user, String companyName, String role){
        if(!user.getClientRoles().containsKey(companyName))
            return false;

        if(!user.getClientRoles().get(companyName).equals(role))
            return false;

        return true;
    }

    public void update(Route route){
        routeRepository.save(route);
    }

    @Transactional
    public void create(CreateRouteDTO createRouteDTO, User user) throws ForbiddenException, TaskNotFoundException {
        Task task = taskService.get(createRouteDTO.getTaskId(), user);
        Route route = new Route();
        route.setTask(task);
        routeRepository.save(route);
        Event event = new Event();
        event.setCreatedAt(LocalDateTime.now());
        event.setStatus(Status.CREATED);
        event.setRoute(route);
        eventRepository.save(event);
    }
}
