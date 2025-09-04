package com.money_manager.controller;

import com.money_manager.dto.request.ExpenseRequest;
import com.money_manager.dto.response.ExpenseResponse;
import com.money_manager.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        ExpenseResponse expenseResponse = expenseService.addExpense(expenseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponse);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getCurrentMonthExpenseForCurrentUser() {
        List<ExpenseResponse> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(expenses);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpenseById(@PathVariable("expenseId") Long expenseId) {
        expenseService.deleteExpenseById(expenseId);
        return ResponseEntity.noContent().build();
    }
}
