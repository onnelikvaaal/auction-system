package ru.skypro.springboot.project.auctionsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skypro.springboot.project.auctionsystem.dto.CreateLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.BidDTO;
import ru.skypro.springboot.project.auctionsystem.dto.FullLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.LotDTO;
import ru.skypro.springboot.project.auctionsystem.entity.Bid;
import ru.skypro.springboot.project.auctionsystem.entity.Lot;
import ru.skypro.springboot.project.auctionsystem.entity.Status;
import ru.skypro.springboot.project.auctionsystem.exceptions.BidNotFoundException;
import ru.skypro.springboot.project.auctionsystem.exceptions.LotNotActiveException;
import ru.skypro.springboot.project.auctionsystem.exceptions.LotNotFoundException;
import ru.skypro.springboot.project.auctionsystem.repository.BidRepository;
import ru.skypro.springboot.project.auctionsystem.repository.LotPagingRepository;
import ru.skypro.springboot.project.auctionsystem.repository.LotRepository;
import ru.skypro.springboot.project.auctionsystem.transformer.LotTransformer;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final LotTransformer lotTransformer;
    private final LotPagingRepository lotPagingRepository;

    public LotServiceImpl(LotRepository lotRepository,
                          BidRepository bidRepository,
                          LotTransformer lotTransformer,
                          LotPagingRepository lotPagingRepository) {
        this.lotRepository = lotRepository;
        this.bidRepository = bidRepository;
        this.lotTransformer = lotTransformer;
        this.lotPagingRepository = lotPagingRepository;
    }

    @Override
    public void createLot(CreateLotDTO createLotDTO) {
        Lot lot = lotTransformer.createLotDTOToLot(createLotDTO);
        lotRepository.save(lot);
    }

    @Override
    public void startBidding(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Lot not found!"));
        if (lot.getStatus() != Status.STARTED) {
            lot.setStatus(Status.STARTED);
            lotRepository.save(lot);
        }
    }

    @Override
    public void stopBidding(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Lot not found!"));
        if (lot.getStatus() != Status.STOPPED) {
            lot.setStatus(Status.STOPPED);
            lotRepository.save(lot);
        }
    }

    @Override
    public String makeABet(Integer id, String bidderName) throws LotNotFoundException, LotNotActiveException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Lot not found!"));
        if (lot.getStatus() != Status.STARTED) {
            throw new LotNotActiveException("Lot is not active at the moment!");
        }
        Bid bid = new Bid();
        bid.setLot(lot);
        bid.setBidderName(bidderName);
        bid.setBidDate(new Date());
        bidRepository.save(bid);
        return bidderName;
    }

    @Override
    public BidDTO findFirstBidder(Integer id) throws LotNotFoundException, BidNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Lot not found!"));
        List<Bid> bidList = bidRepository.findAllByLot(lot);
        Comparator<Bid> comparator = (b1, b2) -> {
            if (b1.getBidDate().after(b2.getBidDate())) {
                return 1;
            } else if (b1.getBidDate().before(b2.getBidDate())) {
                return -1;
            }
            return 0;
        };

        Bid firstBid = bidList.stream().min(comparator)
                .orElseThrow(() -> new BidNotFoundException("Bid not found!"));
        BidDTO bidDTO = new BidDTO();
        bidDTO.setBidderName(firstBid.getBidderName());
        bidDTO.setBidDate(firstBid.getBidDate());
        return bidDTO;
    }

    @Override
    public String findMostFrequentBidder(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Lot not found!"));
        List<Bid> bidList = bidRepository.findAllByLot(lot);
        Map<String, Integer> biddersMap = new HashMap<>();
        for (Bid bid : bidList) {
            Integer value = biddersMap.get(bid.getBidderName());
            if (value == null) {
                biddersMap.put(bid.getBidderName(), 1);
            } else {
                biddersMap.put(bid.getBidderName(), value + 1);
            }
        }
        return biddersMap.keySet().stream()
                .max(Comparator.comparing(biddersMap::get))
                .orElse(null);
    }

    @Override
    public FullLotDTO getFullLotInfo(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Lot not found!"));

        List<Bid> bidList = bidRepository.findAllByLotOrderByBidDateDesc(lot);
        Integer currentPrice = lot.getStartPrice() + (bidList.size() * lot.getBidPrice());

        return lotTransformer.lotToFullLotDTO(lot, currentPrice, bidList.get(0));

    }

    @Override
    public List<LotDTO> getLotsByPage(int pageIndex, Status status) {
        PageRequest page = PageRequest.of(pageIndex, 10);
        Page<Lot> employeePage = lotPagingRepository.findByStatus(status, page);
        return employeePage.stream()
                .map(lotTransformer::lotToLotDTO)
                .collect(Collectors.toList());
    }


}
