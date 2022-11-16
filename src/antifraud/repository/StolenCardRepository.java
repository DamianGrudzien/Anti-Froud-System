package antifraud.repository;

import antifraud.model.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    boolean existsByNumber(String number);

    long deleteByNumber(String number);

    List<StolenCard> findByOrderByIdAsc();



}


