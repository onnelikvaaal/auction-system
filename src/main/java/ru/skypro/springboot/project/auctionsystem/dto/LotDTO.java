package ru.skypro.springboot.project.auctionsystem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skypro.springboot.project.auctionsystem.entity.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class LotDTO {

    private Integer id;
    private Status status;
    @Size(min = 3, max = 64)
    private String title;
    @Size(min = 1, max = 4096)
    private String description;
    @Min(1)
    private Integer startPrice;
    @Min(1)
    private Integer bidPrice;
}
