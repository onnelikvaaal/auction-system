package ru.skypro.springboot.project.auctionsystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotReportPojo {
    private Integer id;
    private String status;
    private String title;
    private String lastBidder;
    private Integer currentPrice;
}
