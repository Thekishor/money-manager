package com.money_manager.service;

import com.money_manager.dto.request.AuthRequest;
import com.money_manager.dto.request.ProfileRequest;
import com.money_manager.dto.response.ProfileResponse;
import com.money_manager.entity.Profile;
import com.money_manager.repository.ProfileRepository;
import com.money_manager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Value("${money.manager.backend.url}")
    private String backend_url;

    @Override
    public ProfileResponse registerProfile(ProfileRequest profileRequest) {
        try {
            if (profileRepository.existsByEmail(profileRequest.getEmail())) {
                throw new RuntimeException("User already exists with email");
            }
            Profile profile = mapProfileRequestToProfileEntity(profileRequest);
            profile.setActivationToken(UUID.randomUUID().toString());
            profile.setActivationTokenExpiry(LocalDateTime.now().plusHours(24));
            Profile savedProfile = profileRepository.save(profile);
            String activation_link = backend_url + "/api/v1.0/activate?token=";
            String url = activation_link + savedProfile.getActivationToken();
            String body = "Click on the following link to activate your account: " + url;
            String subject = "Activate your Money Manager account";
            emailService.sendEmail(savedProfile.getEmail(), subject, body);
            return mapProfileEntityToProfileResponse(savedProfile);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public boolean activateProfile(String activationToken) {
        if (activationToken == null || activationToken.isEmpty()) {
            throw new RuntimeException("Activation token should not be null or empty");
        }
        Profile userProfile = profileRepository.findByActivationToken(activationToken)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));
        if (userProfile.getActivationTokenExpiry().isAfter(LocalDateTime.now())) {
            userProfile.setIsActive(true);
            userProfile.setActivationToken(null);
            userProfile.setActivationTokenExpiry(null);
            profileRepository.save(userProfile);
            return true;
        } else {
            profileRepository.delete(userProfile);
            return false;
        }
    }

    @Override
    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(Profile::getIsActive)
                .orElse(false);
    }

    @Override
    public ProfileResponse getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .map(this::mapProfileEntityToProfileResponse)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public ProfileResponse getPublicProfile(String email) {
        ProfileResponse profileResponse = null;
        if (email == null) {
            return getCurrentProfile();
        } else {
            return profileResponse = profileRepository.findByEmail(email)
                    .map(this::mapProfileEntityToProfileResponse)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email"));
        }
    }

    @Override
    public Map<String, Object> generateToken(AuthRequest authRequest) {
        try {
            authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            String token = jwtUtil.generateToken(authRequest.getEmail());
            return Map.of(
                    "token", token,
                    "expiration", "Your token will expire in 2 hours",
                    "user", getPublicProfile(authRequest.getEmail())
            );
        } catch (Exception exception) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    private ProfileResponse mapProfileEntityToProfileResponse(Profile savedProfile) {
        return ProfileResponse.builder()
                .id(savedProfile.getId())
                .fullName(savedProfile.getFullName())
                .email(savedProfile.getEmail())
                .profileImageUrl(savedProfile.getProfileImageUrl())
                .createdAt(savedProfile.getCreatedAt())
                .updatedAt(savedProfile.getUpdatedAt())
                .build();
    }

    private Profile mapProfileRequestToProfileEntity(ProfileRequest profileRequest) {
        return Profile.builder()
                .fullName(profileRequest.getFullName())
                .email(profileRequest.getEmail())
                .password(passwordEncoder.encode(profileRequest.getPassword()))
                .profileImageUrl(profileRequest.getProfileImageUrl())
                .build();
    }
}
