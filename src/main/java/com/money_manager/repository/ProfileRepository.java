package com.money_manager.repository;

import com.money_manager.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByEmail(String email);

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByActivationToken(String activationToken);

}
