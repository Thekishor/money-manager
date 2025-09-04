package com.money_manager.controller;

import com.money_manager.dto.request.AuthRequest;
import com.money_manager.dto.request.ProfileRequest;
import com.money_manager.dto.response.ProfileResponse;
import com.money_manager.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileResponse> registerProfile(@Valid @RequestBody ProfileRequest profileRequest) {
        ProfileResponse profileResponse = profileService.registerProfile(profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileResponse);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        try {
            boolean isActivated = profileService.activateProfile(token);
            if (isActivated) {
                return ResponseEntity.ok("Profile activated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.GONE)
                        .body("Activation link expired. Please sign up again");
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            if (!profileService.isAccountActive(authRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "message", "Account is not active. Please " +
                                        "activate your account first because activation linked expired within 24 hours."
                        ));
            }
            Map<String, Object> response = profileService.generateToken(authRequest);
            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", exception.getMessage()
                    ));
        }
    }
}
