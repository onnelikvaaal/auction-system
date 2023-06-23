package ru.skypro.springboot.project.auctionsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.springboot.project.auctionsystem.dto.CreateLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.BidDTO;
import ru.skypro.springboot.project.auctionsystem.dto.FullLotDTO;
import ru.skypro.springboot.project.auctionsystem.dto.LotDTO;
import ru.skypro.springboot.project.auctionsystem.entity.Status;
import ru.skypro.springboot.project.auctionsystem.exceptions.BidNotFoundException;
import ru.skypro.springboot.project.auctionsystem.exceptions.LotNotActiveException;
import ru.skypro.springboot.project.auctionsystem.exceptions.LotNotFoundException;
import ru.skypro.springboot.project.auctionsystem.service.LotService;

import java.util.List;

@RestController
@RequestMapping("/lot")
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }


    @GetMapping("/{id}/first")
    public ResponseEntity<BidDTO> findFirstBidder(@PathVariable Integer id) throws LotNotFoundException, BidNotFoundException {
        BidDTO bidDTO = lotService.findFirstBidder(id);
        return new ResponseEntity<>(bidDTO, HttpStatus.OK);
    }


    @GetMapping("/{id}/frequent")
    public ResponseEntity<String> findMostFrequentBidder(@PathVariable Integer id) throws LotNotFoundException {
        String maxBidder = lotService.findMostFrequentBidder(id);
        return new ResponseEntity<>(maxBidder, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<FullLotDTO> getFullLotInfo(@PathVariable Integer id) throws LotNotFoundException {
        FullLotDTO fullLotDTO = lotService.getFullLotInfo(id);
        return new ResponseEntity<>(fullLotDTO, HttpStatus.OK);
    }


    @PostMapping("/{id}/start")
    public void startBidding(@PathVariable Integer id) throws LotNotFoundException {
        lotService.startBidding(id);
    }


    @PostMapping("/{id}/bid")
    public ResponseEntity<String> makeABet(@PathVariable Integer id,
                                           @RequestParam String bidderName) throws LotNotFoundException, LotNotActiveException {
        String result = lotService.makeABet(id, bidderName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/{id}/stop")
    public void stopBidding(@PathVariable Integer id) throws LotNotFoundException {
        lotService.stopBidding(id);
    }


    @PostMapping("/")
    public void createLot(@RequestBody CreateLotDTO createLotDTO) {
        lotService.createLot(createLotDTO);
    }


    @GetMapping("/")
    public List<LotDTO> getLotsByPage(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                      @RequestParam (name = "status") Status status) {
        return lotService.getLotsByPage(page, status);
    }


    //GET: /lot/export Экспортировать все лоты в файл CSV




}
