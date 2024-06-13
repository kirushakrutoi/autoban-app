package ru.kirill.dwhService.models;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "stat")
public class CompanyStat {
    @Id
    @Column(name = "company_name")
    private String companyName;

    @Column(name = "count_ended_route")
    private long countEndedRoute;

    @Column(name = "count_canceled_route")
    private long countCanceledRoute;

    @Column(name = "count_started_route")
    private long countStartedRoute;

    @Column(name = "count_task")
    private long countTask;
}