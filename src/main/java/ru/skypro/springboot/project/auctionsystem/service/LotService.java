package ru.skypro.springboot.project.auctionsystem.service;

import ru.skypro.springboot.project.auctionsystem.dto.CreateLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.BidDTO;
import ru.skypro.springboot.project.auctionsystem.dto.FullLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.LotDTO;
import ru.skypro.springboot.project.auctionsystem.entity.Status;
import ru.skypro.springboot.project.auctionsystem.exceptions.BidNotFoundException;
import ru.skypro.springboot.project.auctionsystem.exceptions.LotNotActiveException;
import ru.skypro.springboot.project.auctionsystem.exceptions.LotNotFoundException;

import java.io.IOException;
import java.util.List;

public interface LotService {

    void createLot(CreateLotDTO createLotDTO);

    void startBidding(Integer id) throws LotNotFoundException;

    void stopBidding(Integer id) throws LotNotFoundException;

    String makeABet(Integer id, String bidderName) throws LotNotFoundException, LotNotActiveException;

    BidDTO findFirstBidder(Integer id) throws LotNotFoundException, BidNotFoundException;

    String findMostFrequentBidder(Integer id) throws LotNotFoundException;

    FullLotDTO getFullLotInfo(Integer id) throws LotNotFoundException;

    List<LotDTO> getLotsByPage(int pageIndex, Status status);

    byte[] createReport() throws IOException, BidNotFoundException;
}
