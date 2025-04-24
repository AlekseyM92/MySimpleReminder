package com.amikhaylov.mysimplereminder.database.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;

public interface ReminderRepositoryService {
    public void saveReminder(Reminder reminder);
    public Reminder getReminder(Long reminderId);
    public List<Reminder> findAllUserReminders(Long chatId);
    public List<Reminder> findAllUserReminders(Long chatId, LocalDate date);
    public List<Reminder> findAllRemindersByDate(LocalDate date, Boolean isDelivered);
    public void deleteReminder(Reminder reminder);
    public void deleteReminders(List<Reminder> reminders);
    public void deleteAllUserReminders(Long chatId);
    public int deleteDeliveredReminders();
    public void updateReminderDelivered(Reminder reminder, boolean delivered);
}
