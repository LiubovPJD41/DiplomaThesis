package ru.polyaeva.DiplomaThesis.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.polyaeva.DiplomaThesis.exception.UnauthorizedException;
import ru.polyaeva.DiplomaThesis.model.User;
import ru.polyaeva.DiplomaThesis.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User Service: Unauthorized");
            throw new UnauthorizedException("User Service: Unauthorized");
        }
        return user;
    }
}