package ru.kirill.logistService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "startPoint", referencedColumnName = "id")
    private Point startPoint;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endPoint", referencedColumnName = "id")
    private Point endPoint;

    @Column(name = "driver_id")
    private String driverId;

    @Column(name = "driver_first_name")
    private String driverFirstName;

    @Column(name = "driver_last_name")
    private String driverLastName;

    @Column(name = "order_description")
    private String orderDescription;

    @Column(name = "state_number")
    private String stateNumber;

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER)
    private List<Route> routes;
}
