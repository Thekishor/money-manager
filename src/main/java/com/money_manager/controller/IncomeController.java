package com.money_manager.controller;

import com.money_manager.dto.request.IncomeRequest;
import com.money_manager.dto.response.IncomeResponse;
import com.money_manager.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponse> addIncome(@Valid @RequestBody IncomeRequest incomeRequest) {
        IncomeResponse incomeResponse = incomeService.addIncome(incomeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeResponse);
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getCurrentMonthIncomeForCurrentUser() {
        List<IncomeResponse> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(incomes);
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteExpenseById(@PathVariable("incomeId") Long incomeId) {
        incomeService.deleteIncomeById(incomeId);
        return ResponseEntity.noContent().build();
    }

}
