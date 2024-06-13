package ru.kirill.dwhService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kirill.dwhService.models.CompanyStat;

import java.util.Optional;

@Repository
public interface StatRepository extends JpaRepository<CompanyStat, Long> {
    Optional<CompanyStat> findByCompanyName(String companyName);
}
