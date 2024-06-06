package ru.kirill.logistService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kirill.logistService.models.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}
