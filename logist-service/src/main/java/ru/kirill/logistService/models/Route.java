package ru.kirill.logistService.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "routes")
public class Route {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToMany(mappedBy = "route", fetch = FetchType.EAGER)
    private List<Event> events;

    @OneToMany(mappedBy = "route", fetch = FetchType.EAGER)
    private List<Point> locations;

    public Route() {
        createdAt = LocalDateTime.now();
        events = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
