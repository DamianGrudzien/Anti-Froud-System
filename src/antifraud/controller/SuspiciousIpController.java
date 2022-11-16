package antifraud.controller;

import antifraud.model.IpAddress;
import antifraud.model.request.IpRequest;
import antifraud.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/api/antifraud")
public class SuspiciousIpController {

    @Autowired
    IpService ipService;

    @PostMapping("/suspicious-ip")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<IpAddress> addIpAddress(@Valid @RequestBody IpRequest ipRequest) {
        IpAddress ipAddress = ipService.addIpAddress(ipRequest);
        return ResponseEntity.ok().body(ipAddress);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<Map<String,String>> deleteIpAddress(@PathVariable String ip) {
        ipService.deleteIpAddress(ip);
        String value = "IP " + ip + " successfully removed!";
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status",value));
    }

    @GetMapping("/suspicious-ip")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<List<IpAddress>> listOfIps() {
        List<IpAddress> allIps = ipService.getAllIps();
        log.info("All ips should be null {}",allIps);
        return ResponseEntity.ok().body(allIps);
    }
}
