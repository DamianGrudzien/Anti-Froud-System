package antifraud.service;

import antifraud.model.IpAddress;
import antifraud.model.request.IpRequest;
import antifraud.repository.IpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IpService {
    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern patternIp = Pattern.compile(IPV4_REGEX);

    @Autowired
    IpRepository ipRepository;
    public IpAddress addIpAddress(IpRequest ipRequest) {
        if (ipRepository.existsByIp(ipRequest.getIp())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        Matcher matcherIp = patternIp.matcher(ipRequest.getIp());
        if (!matcherIp.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        IpAddress ipAddress = new IpAddress();
        ipAddress.setIp(ipRequest.getIp());
        return ipRepository.save(ipAddress);
    }

    @Transactional
    public void deleteIpAddress(String ip) {
        Matcher matcherIp = patternIp.matcher(ip);
        if (!matcherIp.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!ipRepository.existsByIp(ip)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        ipRepository.deleteByIp(ip);
    }

    public List<IpAddress> getAllIps() {
        if (ipRepository.findAll().isEmpty()) {
            return new ArrayList<>();
        }
        return ipRepository.findByOrderByIdAsc();
    }
}
