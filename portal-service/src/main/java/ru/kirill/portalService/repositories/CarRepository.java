package ru.kirill.portalService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kirill.portalService.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    public Car findByVin(String vin);
}
