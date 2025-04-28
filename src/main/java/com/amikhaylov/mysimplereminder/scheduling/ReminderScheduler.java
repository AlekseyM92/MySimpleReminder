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

import java.io.File;
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

    //@Scheduled(cron = "@hourly")
    @Scheduled(cron = "0 * * * * *")
    public void remindAll() {
        List<Reminder> reminders =
                reminderRepositoryService.findAllRemindersByDate(LocalDate.now(), false);
        if (!reminders.isEmpty()) {
            log.info("Reminder started at " + LocalDateTime.now());
            for (Reminder reminder : reminders) {
                try {
                    remindSender.sendReminder(reminder, simpleReminderBot);
                    reminderRepositoryService.updateReminderDelivered(reminder, true);
                    File file = new File(reminder.getFilePath());
                    if (file.exists()) {
                        var isDeleted = file.delete();
                        log.info("File " + reminder.getFilePath() + " is deleted: " + isDeleted);
                    }
                } catch (TelegramApiException e) {
                    log.error("Ошибка отправки напоминания!: " + e);
                }
            }
        }
    }

    //@Scheduled(initialDelay = 120000, fixedRate = 120000)
    @Scheduled(cron = "@hourly")
    public void deleteReminders () {
        int r;
        r = reminderRepositoryService.deleteDeliveredReminders();
        log.info("Deleted " + r + " reminders");
    }
}