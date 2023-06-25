package ru.skypro.springboot.project.auctionsystem.transformer;

import org.springframework.stereotype.Component;
import ru.skypro.springboot.project.auctionsystem.dto.BidDTO;
import ru.skypro.springboot.project.auctionsystem.dto.CreateLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.FullLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.LotDTO;
import ru.skypro.springboot.project.auctionsystem.entity.Bid;
import ru.skypro.springboot.project.auctionsystem.entity.Lot;

@Component
public class LotTransformer {

    public LotDTO lotToLotDTO(Lot lot) {
        if (lot == null) {
            return null;
        }

        LotDTO lotDTO = new LotDTO();
        lotDTO.setId(lot.getId());
        lotDTO.setStatus(lot.getStatus());
        lotDTO.setTitle(lot.getTitle());
        lotDTO.setDescription(lot.getDescription());
        lotDTO.setStartPrice(lot.getStartPrice());
        lotDTO.setBidPrice(lot.getBidPrice());
        return lotDTO;
    }

    public Lot lotDTOToLot(LotDTO lotDTO) {
        if (lotDTO == null) {
            return null;
        }

        Lot lot = new Lot();
        lot.setId(lotDTO.getId());
        lot.setStatus(lotDTO.getStatus());
        lot.setTitle(lotDTO.getTitle());
        lot.setDescription(lotDTO.getDescription());
        lot.setStartPrice(lotDTO.getStartPrice());
        lot.setBidPrice(lotDTO.getBidPrice());
        return lot;
    }

    public Lot createLotDTOToLot(CreateLotDTO createLotDTO) {
        if (createLotDTO == null) {
            return null;
        }

        Lot lot = new Lot();
        lot.setTitle(createLotDTO.getTitle());
        lot.setDescription(createLotDTO.getDescription());
        lot.setStartPrice(createLotDTO.getStartPrice());
        lot.setBidPrice(createLotDTO.getBidPrice());
        return lot;
    }

    public FullLotDTO lotToFullLotDTO(Lot lot, Integer currentPrice, Bid lastBid) {
        if (lot == null) {
            return null;
        }
        FullLotDTO fullLotDTO = new FullLotDTO();
        fullLotDTO.setId(lot.getId());
        fullLotDTO.setStatus(lot.getStatus());
        fullLotDTO.setTitle(lot.getTitle());
        fullLotDTO.setDescription(lot.getDescription());
        fullLotDTO.setStartPrice(lot.getStartPrice());
        fullLotDTO.setBidPrice(lot.getBidPrice());
        fullLotDTO.setCurrentPrice(currentPrice);

        BidDTO bidDTO = new BidDTO();
        bidDTO.setBidderName(lastBid.getBidderName());
        bidDTO.setBidDate(lastBid.getBidDate());

        fullLotDTO.setLastBid(bidDTO);
        return fullLotDTO;
    }
}
