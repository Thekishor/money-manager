package com.money_manager.service;

import com.money_manager.dto.request.IncomeRequest;
import com.money_manager.dto.response.IncomeResponse;
import com.money_manager.dto.response.ProfileResponse;
import com.money_manager.entity.Category;
import com.money_manager.entity.Income;
import com.money_manager.entity.Profile;
import com.money_manager.repository.CategoryRepository;
import com.money_manager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final CategoryRepository categoryRepository;

    private final IncomeRepository incomeRepository;

    private final ProfileService profileService;

    @Override
    public IncomeResponse addIncome(IncomeRequest incomeRequest) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(incomeRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Income income = mapIncomeRequestToIncomeEntity(incomeRequest, category, profileResponse);
        Income savedIncome = incomeRepository.save(income);
        return mapIncomeToIncomeResponse(savedIncome);
    }

    @Override
    public List<IncomeResponse> getCurrentMonthIncomesForCurrentUser() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        LocalDate date = LocalDate.now();
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
        return incomeRepository.findByProfileIdAndDateBetween(profileResponse.getId(), startDate, endDate)
                .stream().map(this::mapIncomeToIncomeResponse).toList();
    }

    @Override
    public void deleteIncomeById(Long incomeId) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!income.getProfile().getId().equals(profileResponse.getId())) {
            throw new RuntimeException("Unauthorized to delete");
        }
        incomeRepository.delete(income);
    }

    @Override
    public List<IncomeResponse> findLatest5IncomesForCurrentUser() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        return incomeRepository.findTop5ByProfileIdOrderByDateDesc(profileResponse.getId())
                .stream().map(this::mapIncomeToIncomeResponse).toList();
    }

    @Override
    public BigDecimal getTotalIncomesForCurrentUser() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profileResponse.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<IncomeResponse> filterByIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        return incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profileResponse.getId(), startDate, endDate, keyword, sort)
                .stream().map(this::mapIncomeToIncomeResponse).toList();
    }


    private Income mapIncomeRequestToIncomeEntity(IncomeRequest incomeRequest, Category category, ProfileResponse profileResponse) {
        return Income.builder()
                .name(incomeRequest.getName())
                .icon(incomeRequest.getIcon())
                .date(incomeRequest.getDate())
                .amount(incomeRequest.getAmount())
                .category(category)
                .profile(profileSaved(profileResponse))
                .build();
    }

    private IncomeResponse mapIncomeToIncomeResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : "N/A")
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }

    private Profile profileSaved(ProfileResponse profileResponse) {
        return Profile.builder()
                .id(profileResponse.getId())
                .fullName(profileResponse.getFullName())
                .email(profileResponse.getEmail())
                .profileImageUrl(profileResponse.getProfileImageUrl())
                .build();
    }
}
