package ru.kirill.logistService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kirill.logistService.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
