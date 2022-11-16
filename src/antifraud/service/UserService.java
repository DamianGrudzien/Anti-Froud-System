package antifraud.service;

import antifraud.model.request.UserAccessChange;
import antifraud.model.enums.Role;
import antifraud.model.User;
import antifraud.model.enums.UserStatus;
import antifraud.model.request.ChangeRoleRequest;
import antifraud.model.request.UserRequest;
import antifraud.model.response.UserResponse;
import antifraud.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    @Lazy
    PasswordEncoder passwordEncoder;
    ModelMapper modelMapper;

    public UserResponse addUser(UserRequest userRequest) {
        if (userRepository.existsByUsernameIgnoreCase(userRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        if (userRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMINISTRATOR);
            user.setUserStatus(UserStatus.UNLOCK);
        } else {
            user.setRole(Role.MERCHANT);
            user.setUserStatus(UserStatus.LOCK);
        }

        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                             .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public List<UserResponse> listOfUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(user -> modelMapper.map(user, UserResponse.class))
                             .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        ifNotExistsThrowExceptionWithStatus(username, HttpStatus.NOT_FOUND);
        userRepository.deleteByUsernameIgnoreCase(username);
    }

    public UserResponse changeRole(@Valid ChangeRoleRequest userRequest) {
        ifNotExistsThrowExceptionWithStatus(userRequest.getUsername(), HttpStatus.NOT_FOUND);
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(userRequest.getUsername());

        if (userRequest.getRole().equals(Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();

        if (user.getRole().equals(userRequest.getRole())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        user.setRole(userRequest.getRole());
        userRepository.save(user);
        return modelMapper.map(user,UserResponse.class);
    }

    public User changeAccess(UserAccessChange userAccess) {
        ifNotExistsThrowExceptionWithStatus(userAccess.getUsername(),HttpStatus.NOT_FOUND);
        Optional<User> userOptional = userRepository.findByUsername(userAccess.getUsername());

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();

        if (user.getRole().toString().equals(Role.ADMINISTRATOR.toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        user.setUserStatus(userAccess.getOperation());
        userRepository.save(user);
        return user;
    }

    private void ifNotExistsThrowExceptionWithStatus(String username, HttpStatus status) {
        if (!userRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(status);
        }
    }
}