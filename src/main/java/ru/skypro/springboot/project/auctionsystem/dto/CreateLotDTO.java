package ru.skypro.springboot.project.auctionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLotDTO {

    private String title;
    private String description;
    private Integer startPrice;
    private Integer bidPrice;
}
