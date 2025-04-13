package com.amikhaylov.mysimplereminder.database.service;

import com.amikhaylov.mysimplereminder.database.entity.Reminder;

import java.time.LocalDate;
import java.util.List;

public interface ReminderRepositoryService {
    public void saveReminder(Reminder reminder);
    public List<Reminder> findAllUserReminders(Long chatId);
    public List<Reminder> findAllUserReminders(Long chatId, LocalDate date);
    public List<Reminder> findAllRemindersByDate(LocalDate date, Boolean isDelivered);
    public void deleteReminder(Reminder reminder);
    public void deleteReminders(List<Reminder> reminders);
    public void deleteAllUserReminders(Long chatId);
    public void deleteDeliveredReminders();
}
