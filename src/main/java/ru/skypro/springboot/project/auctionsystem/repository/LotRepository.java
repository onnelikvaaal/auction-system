package ru.skypro.springboot.project.auctionsystem.repository;

import org.springframework.data.repository.CrudRepository;
import ru.skypro.springboot.project.auctionsystem.entity.Lot;

public interface LotRepository extends CrudRepository<Lot, Integer> {
}
