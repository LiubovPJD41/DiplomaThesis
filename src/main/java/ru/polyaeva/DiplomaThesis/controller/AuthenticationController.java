package ru.polyaeva.DiplomaThesis.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.polyaeva.DiplomaThesis.dto.request.AuthenticationRQ;
import ru.polyaeva.DiplomaThesis.dto.response.AuthenticationRS;
import ru.polyaeva.DiplomaThesis.service.AuthenticationService;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthenticationRS login(@RequestBody AuthenticationRQ authenticationRQ) {
        return new AuthenticationRS(authenticationService.login(authenticationRQ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String authToken) {
        authenticationService.logout(authToken);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}