package ru.kirill.logistService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kirill.logistService.models.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
}
