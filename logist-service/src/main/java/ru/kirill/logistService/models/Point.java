package ru.kirill.logistService.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "points")
public class Point {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "lat")
    @NotNull
    private BigDecimal latitude;
    @NotNull
    @Column(name = "lon")
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}
