package ru.kirill.logistService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.logistService.exceptions.ForbiddenException;
import ru.kirill.logistService.exceptions.RouteNotFoundException;
import ru.kirill.logistService.mappers.Mapper;
import ru.kirill.logistService.models.DTOs.CreatePointDTO;
import ru.kirill.logistService.models.Point;
import ru.kirill.logistService.models.Route;
import ru.kirill.logistService.repositories.PointRepository;

@Service
public class PointService {
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private RouteService routeService;

    @Transactional
    public void create(CreatePointDTO pointDTO) throws RouteNotFoundException {
        Route route = routeService.get(pointDTO.getRouteId());
        Point point = Mapper.convertToPoint(pointDTO);
        point.setRoute(route);
        pointRepository.save(point);
    }
}
