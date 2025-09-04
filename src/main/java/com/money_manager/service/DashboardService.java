package com.money_manager.service;

import com.money_manager.dto.response.ExpenseResponse;
import com.money_manager.dto.response.IncomeResponse;
import com.money_manager.dto.response.ProfileResponse;
import com.money_manager.dto.response.RecentTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileResponse profileResponse = profileService.getCurrentProfile();
        Map<String, Object> value = new LinkedHashMap<>();
        List<IncomeResponse> latestIncomes = incomeService.findLatest5IncomesForCurrentUser();
        List<ExpenseResponse> latestExpenses = expenseService.findLatest5ExpenseForCurrentUser();
        List<RecentTransactionResponse> recentTransactionResponses = concat(latestIncomes.stream().map(income ->
                        RecentTransactionResponse.builder()
                                .id(income.getId())
                                .profileId(profileResponse.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("Income")
                                .build()),
                latestExpenses.stream().map(expense ->
                        RecentTransactionResponse.builder()
                                .id(expense.getId())
                                .profileId(profileResponse.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("Expense")
                                .build()
                )).sorted((a, b) -> {
            int compare = b.getDate().compareTo(a.getDate());
            if (compare == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return compare;
        }).toList();

        value.put("totalBalance",
                incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpenseForCurrentUser()));
        value.put("totalIncome", incomeService.getTotalIncomesForCurrentUser());
        value.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        value.put("recent5Expenses", latestExpenses);
        value.put("recent5Incomes", latestIncomes);
        value.put("recentTransactions", recentTransactionResponses);
        return value;
    }
}
