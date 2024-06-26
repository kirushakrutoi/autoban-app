package ru.kirill.portalService.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String vin;
    @Column
    private int year;
    @Column
    private String company;
    @Column(unique = true)
    private String stateNumber;
}
