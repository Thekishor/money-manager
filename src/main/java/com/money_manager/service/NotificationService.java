package com.money_manager.service;

import com.money_manager.dto.response.ExpenseResponse;
import com.money_manager.entity.Profile;
import com.money_manager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *")
    public void sendDailyIncomeExpensesReminder() {
        log.info("Job started: send daily income expenses reminder");
        List<Profile> profiles = profileRepository.findAll();
        for (Profile profile : profiles) {
            String body = "Hi " + profile.getFullName() + ", <br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                    + "<a href='" + frontendUrl + "' style='display:inline-block;padding:10px 20px;background-color:#007bff;color:white;text-decoration:none;border-radius:5px;'>Open Money Manager</a>"
                    + "<br><br>Best regards,<br>Money Manager Team";
            emailService.sendEmail(profile.getEmail(), "Daily reminder: Add your income and expenses", body);
        }
        log.info("Job completed: send daily income and expenses reminder");
    }

    @Scheduled(cron = "0 0 23 * * *")
    public void sendDailyExpensesSummary() {
        log.info("Job started: send daily expenses summary");
        List<Profile> profiles = profileRepository.findAll();
        for (Profile profile : profiles) {
            List<ExpenseResponse> expense =
                    expenseService.getExpenseForUserOnDate(profile.getId(), LocalDate.now());
            if (!expense.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd;padding:8px;'>S.No</th><th style='border:1px solid #ddd;padding:8px;'>Name</th><th style='border:1px solid #ddd;padding:8px;'>Amount</th><th style='border:1px solid #ddd;padding:8px;'>Category</th></tr>");

                int i = 1;
                for (ExpenseResponse expenseResponse : expense) {
                    table.append("<tr>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseResponse.getName()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseResponse.getAmount()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseResponse.getCategoryName()).append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body = "Hi " + profile.getFullName() + ",<br/><br/> Here is a summary of your expenses for today:<br/><br/>" + table.toString() + "<br/><br/>Best regards,<br/>Money Manager Team";

                emailService.sendEmail(profile.getEmail(), "Your daily Expense summary", body);
            }
        }
        log.info("Job completed: send daily expenses summary");
    }
}
