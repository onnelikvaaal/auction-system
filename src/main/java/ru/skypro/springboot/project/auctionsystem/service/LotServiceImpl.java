package ru.skypro.springboot.project.auctionsystem.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
import ru.skypro.springboot.project.auctionsystem.pojo.LotReportPojo;
import ru.skypro.springboot.project.auctionsystem.repository.BidRepository;
import ru.skypro.springboot.project.auctionsystem.repository.LotPagingRepository;
import ru.skypro.springboot.project.auctionsystem.repository.LotRepository;
import ru.skypro.springboot.project.auctionsystem.transformer.LotTransformer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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
        Lot createdLot = lotRepository.save(lot);
        log.info("Successfully created new lot; id = {}, title = {}", createdLot.getId(), createdLot.getTitle() );
    }

    @Override
    public void startBidding(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Not found lot with id = " + id));
        if (lot.getStatus() != Status.STARTED) {
            lot.setStatus(Status.STARTED);
            lotRepository.save(lot);
            log.info("Bidding is started");
        }
    }

    @Override
    public void stopBidding(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Not found lot with id = " + id));
        if (lot.getStatus() != Status.STOPPED) {
            lot.setStatus(Status.STOPPED);
            lotRepository.save(lot);
            log.info("Bidding is stopped");
        }
    }

    @Override
    public String makeABet(Integer id, String bidderName) throws LotNotFoundException, LotNotActiveException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Not found lot with id = " + id));
        if (lot.getStatus() != Status.STARTED) {
            throw new LotNotActiveException("Lot is not active at the moment!");
        }
        Bid bid = new Bid();
        bid.setLot(lot);
        bid.setBidderName(bidderName);
        bid.setBidDate(new Date());
        bidRepository.save(bid);
        log.info("A bid is successfully made by {}", bid.getBidderName());
        return bidderName;

    }

    @Override
    public BidDTO findFirstBidder(Integer id) throws LotNotFoundException, BidNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Not found lot with id = " + id));
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
        log.info("First bidder of the lot with id = {} is successfully found!", id);
        return bidDTO;
    }

    @Override
    public String findMostFrequentBidder(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Not found lot with id = " + id));
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
        String foundBidder = biddersMap.keySet().stream()
                .max(Comparator.comparing(biddersMap::get))
                .orElse(null);
        log.info("The most frequent bidder of the lot with id = {} is successfully found", id);
        return foundBidder;
    }

    @Override
    public FullLotDTO getFullLotInfo(Integer id) throws LotNotFoundException {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException("Not found lot with id = " + id));

        List<Bid> bidList = bidRepository.findAllByLotOrderByBidDateDesc(lot);
        Integer currentPrice = lot.getStartPrice() + (bidList.size() * lot.getBidPrice());

        FullLotDTO fullLotDTO = lotTransformer.lotToFullLotDTO(lot, currentPrice, bidList.get(0));
        log.info("Full lot info is successfully got");
        return fullLotDTO;
    }

    @Override
    public List<LotDTO> getLotsByPage(int pageIndex, Status status) {
        PageRequest page = PageRequest.of(pageIndex, 10);
        Page<Lot> employeePage = lotPagingRepository.findByStatus(status, page);
        log.info("Successfully found employees on page = {}", pageIndex);
        return employeePage.stream()
                .map(lotTransformer::lotToLotDTO)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] createReport() throws IOException, BidNotFoundException {
        List<Lot> lotList = (List<Lot>) lotRepository.findAll();
        List<Bid> bidList = (List<Bid>) bidRepository.findAll();

        List<LotReportPojo> reportPojoList = new ArrayList<>();
        for (Lot lot : lotList) {
            LotReportPojo pojo = new LotReportPojo();
            pojo.setId(lot.getId());
            pojo.setTitle(lot.getTitle());
            pojo.setStatus(lot.getStatus().name());
            pojo.setLastBidder(getLastBidder(lot, bidList));
            pojo.setCurrentPrice(getCurrentPrice(lot, bidList));
            reportPojoList.add(pojo);
        }

        String[] HEADERS = {"id", "title", "status", "lastBidder", "currentPrice"};

        StringWriter sw = new StringWriter();

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build();

        try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            reportPojoList.forEach(p -> {
                try {
                    printer.printRecord(p.getId(), p.getTitle(), p.getStatus(), p.getCurrentPrice(), p.getLastBidder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        log.info("Report is successfully created");
        return sw.toString().getBytes();
    }

    private String getLastBidder(Lot lot, List<Bid> bidList) {
        List<Bid> bidsByLot = bidList.stream().filter(b -> b.getLot().equals(lot)).toList();
        Comparator<Bid> comparator = (b1, b2) -> {
            if (b1.getBidDate().after(b2.getBidDate())) {
                return 1;
            } else if (b1.getBidDate().before(b2.getBidDate())) {
                return -1;
            }
            return 0;
        };

        Bid lastBid = bidsByLot.stream().max(comparator).orElse(null);
        if (lastBid != null) {
            return lastBid.getBidderName();
        } else {
            return "";
        }
    }

    private Integer getCurrentPrice(Lot lot, List<Bid> bidList) {
        List<Bid> bidsByLot = bidList.stream().filter(b -> b.getLot().equals(lot)).toList();
        return lot.getStartPrice() + (bidsByLot.size() * lot.getBidPrice());
    }
}
