package antifraud.controller;


import antifraud.model.User;
import antifraud.model.request.UserAccessChange;
import antifraud.model.request.ChangeRoleRequest;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor

public class UserController {

    @Lazy
    private UserService userService;


    @PostMapping("/user")
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.addUser(userRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('SUPPORT')")
    @GetMapping("/list")
    public List<UserResponse> listOfUsers() {
        return userService.listOfUsers();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping({"/user/{username}", "/user/"})
    public Map<String, String> deleteByUsername(@PathVariable(required = false) String username) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userService.deleteUserByUsername(username);
        return Map.of("username", username, "status", "Deleted successfully!");
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/role")
    public ResponseEntity<UserResponse> changeRole(@Valid @RequestBody ChangeRoleRequest request) {
        UserResponse userResponse = userService.changeRole(request);
        return new ResponseEntity<>(userResponse,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/access")
    public ResponseEntity<Map<String, String>> changeAccess(@Valid @RequestBody UserAccessChange userAccess) {
        User user = userService.changeAccess(userAccess);
        String value = "User " + user.getUsername() + " " + user.getUserStatus().toString().toLowerCase() + "ed!";
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", value));
    }
}
