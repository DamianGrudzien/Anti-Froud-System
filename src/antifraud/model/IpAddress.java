package antifraud.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IpAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String ip;
}
