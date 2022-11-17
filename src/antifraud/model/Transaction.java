package antifraud.model;


import antifraud.model.enums.Region;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Long amount;
    String ip;
    String number;
    @Enumerated(EnumType.STRING)
    Region region;
    LocalDateTime date;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return amount.equals(that.amount) && ip.equals(that.ip) && number.equals(that.number) && region.equals(
                that.region) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, ip, number, region, date);
    }
}
