package com.amikhaylov.mysimplereminder.scheduling;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import com.amikhaylov.mysimplereminder.database.service.ReminderRepositoryService;
import com.amikhaylov.mysimplereminder.service.RemindSender;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j
@Component
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class ReminderScheduler {
    private final ReminderRepositoryService reminderRepositoryService;
    private final RemindSender remindSender;
    private final SimpleReminderBot simpleReminderBot;

    //@Scheduled(cron = "@daily")
    @Scheduled(cron = "0 * * * * *")
    public void remindAll() {
        List<Reminder> reminders =
                reminderRepositoryService.findAllRemindersByDate(LocalDate.now(), false);
        if (!reminders.isEmpty()) {
            log.info("Reminder started at " + LocalDateTime.now());
            for (Reminder reminder : reminders) {
                try {
                    remindSender.sendReminder(reminder, simpleReminderBot);
                } catch (TelegramApiException e) {
                    log.error("Ошибка отправки напоминания!: " + e);
                }
            }
        }
    }
}