package com.money_manager.service;

import com.money_manager.dto.request.ExpenseRequest;
import com.money_manager.dto.response.ExpenseResponse;
import com.money_manager.dto.response.ProfileResponse;
import com.money_manager.entity.Category;
import com.money_manager.entity.Expense;
import com.money_manager.entity.Profile;
import com.money_manager.repository.CategoryRepository;
import com.money_manager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final CategoryRepository categoryRepository;

    private final ExpenseRepository expenseRepository;

    private final ProfileService profileService;

    @Override
    public ExpenseResponse addExpense(ExpenseRequest expenseRequest) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(expenseRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Expense expense = mapExpenseRequestToExpenseEntity(expenseRequest, profileResponse, category);
        Expense savedExpense = expenseRepository.save(expense);
        return mapExpenseToExpenseResponse(savedExpense);
    }

    @Override
    public List<ExpenseResponse> getCurrentMonthExpensesForCurrentUser() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        LocalDate date = LocalDate.now();
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
        return expenseRepository.findByProfileIdAndDateBetween(profileResponse.getId(), startDate, endDate)
                .stream().map(this::mapExpenseToExpenseResponse).toList();

    }

    @Override
    public void deleteExpenseById(Long expenseId) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getProfile().getId().equals(profileResponse.getId())) {
            throw new RuntimeException("Unauthorized to delete");
        }
        expenseRepository.delete(expense);
    }

    @Override
    public List<ExpenseResponse> findLatest5ExpenseForCurrentUser() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        return expenseRepository.findTop5ByProfileIdOrderByDateDesc(profileResponse.getId())
                .stream().map(this::mapExpenseToExpenseResponse).toList();
    }

    @Override
    public BigDecimal getTotalExpenseForCurrentUser() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profileResponse.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<ExpenseResponse> filterByExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        return expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profileResponse.getId(), startDate, endDate, keyword, sort)
                .stream().map(this::mapExpenseToExpenseResponse).toList();
    }

    @Override
    public List<ExpenseResponse> getExpenseForUserOnDate(Long profileId, LocalDate date) {
        return expenseRepository.findByProfileIdAndDate(profileId, date)
                .stream().map(this::mapExpenseToExpenseResponse).toList();
    }

    private Expense mapExpenseRequestToExpenseEntity(ExpenseRequest expenseRequest, ProfileResponse profileResponse, Category category) {
        return Expense.builder()
                .name(expenseRequest.getName())
                .icon(expenseRequest.getIcon())
                .amount(expenseRequest.getAmount())
                .date(expenseRequest.getDate())
                .profile(profileSaved(profileResponse))
                .category(category)
                .build();
    }

    private ExpenseResponse mapExpenseToExpenseResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "N/A")
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
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
