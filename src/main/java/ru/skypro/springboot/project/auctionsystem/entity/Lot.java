package ru.skypro.springboot.project.auctionsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lot")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;
    private String title;
    private String description;
    private Integer startPrice;
    private Integer bidPrice;
}
