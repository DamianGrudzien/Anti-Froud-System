package antifraud.repository;

import antifraud.model.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IpRepository extends JpaRepository<IpAddress,Long> {
    boolean existsByIp(String ip);

    List<IpAddress> findByOrderByIdAsc();

    void deleteByIp(String ip);
}
