package com.amikhaylov.mysimplereminder.database.service;

import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import com.amikhaylov.mysimplereminder.database.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReminderRepositoryServiceImpl implements ReminderRepositoryService {
    private final ReminderRepository reminderRepository;

    @Autowired
    public ReminderRepositoryServiceImpl(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Override
    public void saveReminder(Reminder reminder) {
        reminderRepository.save(reminder);
    }

    @Override
    public List<Reminder> findAllUserReminders(Long chatId) {
        return reminderRepository.findAllByChatId(chatId);
    }

    @Override
    public List<Reminder> findAllUserReminders(Long chatId, LocalDate date) {
        return reminderRepository.findAllByChatIdAndDateReminder(chatId, date);
    }

    @Override
    public List<Reminder> findAllRemindersByDate(LocalDate date, Boolean isDelivered) {
        return reminderRepository.findAllByDateReminderAndDeliveredIs(date, isDelivered);
    }

    @Override
    public void deleteReminder(Reminder reminder) {
        reminderRepository.delete(reminder);
    }

    @Override
    public void deleteReminders(List<Reminder> reminders) {
        reminderRepository.deleteAll(reminders);
    }

    @Override
    public void deleteAllUserReminders(Long chatId) {
        reminderRepository.deleteAllByChatId(chatId);
    }

    @Override
    public void deleteDeliveredReminders() {
        reminderRepository.deleteAllByDeliveredIs(true);
    }
}
