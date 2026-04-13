package edu.rutmiit.demo.demorest.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "halts")
public class HaltEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity city;
    private LocalDateTime time;
}
