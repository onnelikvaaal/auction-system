package ru.skypro.springboot.project.auctionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skypro.springboot.project.auctionsystem.entity.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotDTO {

    private Integer id;
    private Status status;
    private String title;
    private String description;
    private Integer startPrice;
    private Integer bidPrice;
}
