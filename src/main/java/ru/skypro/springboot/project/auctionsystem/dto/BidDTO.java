package ru.skypro.springboot.project.auctionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidDTO {
    private String bidderName;
    private Date bidDate;
}
