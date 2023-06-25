package ru.skypro.springboot.project.auctionsystem.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/lot")
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }


    @GetMapping("/{id}/first")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found first bidder",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BidDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot or Bid not found",
                    content = @Content) })
    public ResponseEntity<BidDTO> findFirstBidder(@PathVariable Integer id) throws LotNotFoundException, BidNotFoundException {
        BidDTO bidDTO = lotService.findFirstBidder(id);
        return new ResponseEntity<>(bidDTO, HttpStatus.OK);
    }


    @GetMapping("/{id}/frequent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found most frequent bidder",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot or Bid not found",
                    content = @Content) })
    public ResponseEntity<String> findMostFrequentBidder(@PathVariable Integer id) throws LotNotFoundException {
        String maxBidder = lotService.findMostFrequentBidder(id);
        return new ResponseEntity<>(maxBidder, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found full lot info",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FullLotDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot or Bid not found",
                    content = @Content) })
    public ResponseEntity<FullLotDTO> getFullLotInfo(@PathVariable Integer id) throws LotNotFoundException {
        FullLotDTO fullLotDTO = lotService.getFullLotInfo(id);
        return new ResponseEntity<>(fullLotDTO, HttpStatus.OK);
    }


    @PostMapping("/{id}/start")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bidding is started",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot or Bid not found",
                    content = @Content) })
    public void startBidding(@PathVariable Integer id) throws LotNotFoundException {
        lotService.startBidding(id);
    }


    @PostMapping("/{id}/bid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bet is made",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot or Bid not found",
                    content = @Content) })
    public ResponseEntity<String> makeABet(@PathVariable Integer id,
                                           @RequestParam String bidderName) throws LotNotFoundException, LotNotActiveException {
        String result = lotService.makeABet(id, bidderName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/{id}/stop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bidding is stopped",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot not found",
                    content = @Content) })
    public void stopBidding(@PathVariable Integer id) throws LotNotFoundException {
        lotService.stopBidding(id);
    }


    @PostMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lot is created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content) })
    public void createLot(@RequestBody @Valid CreateLotDTO createLotDTO) {
        lotService.createLot(createLotDTO);
    }


    @GetMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found lots page",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content) })
    public List<LotDTO> getLotsByPage(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                      @RequestParam (name = "status") Status status) {
        return lotService.getLotsByPage(page, status);
    }

    @GetMapping("/export")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV report created",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Error occurred",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot or Bid not found",
                    content = @Content) })
    public ResponseEntity<Resource> getLotsReport() throws IOException, BidNotFoundException {
        String fileName = "report.csv";
        Resource resource = new ByteArrayResource(lotService.createReport());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(resource);
    }
}
