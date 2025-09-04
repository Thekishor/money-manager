package com.money_manager.service;

import com.money_manager.dto.request.IncomeRequest;
import com.money_manager.dto.response.IncomeResponse;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {

    IncomeResponse addIncome(IncomeRequest incomeRequest);

    List<IncomeResponse> getCurrentMonthIncomesForCurrentUser();

    void deleteIncomeById(Long incomeId);

    List<IncomeResponse> findLatest5IncomesForCurrentUser();

    BigDecimal getTotalIncomesForCurrentUser();

    List<IncomeResponse> filterByIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort);
}
