package com.money_manager.service;

import com.money_manager.dto.request.ExpenseRequest;
import com.money_manager.dto.response.ExpenseResponse;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    ExpenseResponse addExpense(ExpenseRequest expenseRequest);

    List<ExpenseResponse> getCurrentMonthExpensesForCurrentUser();

    void deleteExpenseById(Long expenseId);

    List<ExpenseResponse> findLatest5ExpenseForCurrentUser();

    BigDecimal getTotalExpenseForCurrentUser();

    List<ExpenseResponse> filterByExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    List<ExpenseResponse> getExpenseForUserOnDate(Long profileId, LocalDate date);
}
