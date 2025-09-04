package com.money_manager.service;

import com.money_manager.dto.request.AuthRequest;
import com.money_manager.dto.request.ProfileRequest;
import com.money_manager.dto.response.ProfileResponse;

import java.util.Map;

public interface ProfileService {

    ProfileResponse registerProfile(ProfileRequest profileRequest);

    boolean activateProfile(String activationToken);

    boolean isAccountActive(String email);

    ProfileResponse getCurrentProfile();

    ProfileResponse getPublicProfile(String email);

    Map<String, Object> generateToken(AuthRequest authRequest);
}
