package ru.skypro.springboot.project.auctionsystem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class CreateLotDTO {
    @Size(min = 3, max = 64)
    private String title;
    @Size(min = 1, max = 4096)
    private String description;
    @Min(1)
    private Integer startPrice;
    @Min(1)
    private Integer bidPrice;
}
