package ru.skypro.springboot.project.auctionsystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.skypro.springboot.project.auctionsystem.entity.Bid;
import ru.skypro.springboot.project.auctionsystem.entity.Lot;

import java.util.List;

public interface BidRepository extends CrudRepository<Bid, Integer> {

    List<Bid> findAllByLot(Lot lot);

    List<Bid> findAllByLotOrderByBidDateDesc(Lot lot);

}
