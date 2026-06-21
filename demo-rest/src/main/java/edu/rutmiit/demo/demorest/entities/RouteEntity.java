package edu.rutmiit.demo.demorest.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "routs")
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeTransport;
    private String typeDistance;
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<HaltEntity> halts;
}
