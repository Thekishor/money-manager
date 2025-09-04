package com.money_manager.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterRequest {

    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
    private String sortField;
    private String sortOrder;
}
