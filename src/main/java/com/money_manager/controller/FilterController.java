package com.money_manager.controller;

import com.money_manager.dto.request.FilterRequest;
import com.money_manager.dto.response.ExpenseResponse;
import com.money_manager.dto.response.IncomeResponse;
import com.money_manager.service.ExpenseService;
import com.money_manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransaction(@RequestBody FilterRequest filterRequest) {
        LocalDate startDate = filterRequest.getStartDate() != null ? filterRequest.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterRequest.getEndDate() != null ? filterRequest.getEndDate() : LocalDate.now();
        String keyword = filterRequest.getKeyword() != null ? filterRequest.getKeyword() : "";
        String sortField = filterRequest.getSortField() != null ? filterRequest.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filterRequest.getSortOrder()) ? Sort.Direction.DESC :
                Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if ("income".equals(filterRequest.getType())) {
            List<IncomeResponse> incomeResponses =
                    incomeService.filterByIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomeResponses);
        } else if ("expense".equals(filterRequest.getType())) {
            List<ExpenseResponse> expenseResponses =
                    expenseService.filterByExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenseResponses);
        } else {
            return ResponseEntity.badRequest()
                    .body("Invalid type. Must be 'income' or 'expense'");
        }
    }
}
