package bearhug.management.app.presentation.controller;

import bearhug.management.app.presentation.dto.AuthRequest;
import bearhug.management.app.presentation.dto.AuthResponse;
import bearhug.management.app.service.implementation.UserDetailServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserDetailServiceImpl userDetailService;

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.ok(userDetailService.authenticate(authRequest));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailService.register(authRequest));
    }
}
