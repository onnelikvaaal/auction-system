package ru.skypro.springboot.project.auctionsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.skypro.springboot.project.auctionsystem.entity.Lot;
import ru.skypro.springboot.project.auctionsystem.entity.Status;

import java.util.List;

public interface LotPagingRepository extends PagingAndSortingRepository<Lot, Integer> {

    Page<Lot> findByStatus(Status status, Pageable pageable);
}
