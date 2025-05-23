package com.amikhaylov.mysimplereminder.database.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import com.amikhaylov.mysimplereminder.database.repository.ReminderRepository;
import com.amikhaylov.mysimplereminder.service.TextSaver;
import com.amikhaylov.mysimplereminder.service.VoiceSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReminderRepositoryServiceImpl implements ReminderRepositoryService {
    private final ReminderRepository reminderRepository;
    private final TextSaver textSaver;
    private final VoiceSaver voiceSaver;

    @Autowired
    public ReminderRepositoryServiceImpl(
            ReminderRepository reminderRepository,
            TextSaver textSaver,
            VoiceSaver voiceSaver) {
        this.reminderRepository = reminderRepository;
        this.textSaver = textSaver;
        this.voiceSaver = voiceSaver;
    }

    @Override
    public void saveReminder(Reminder reminder) {
        reminderRepository.save(reminder);
    }

    @Override
    public Reminder getReminder(Long reminderId) {
        return reminderRepository.findById(reminderId).orElse(null);
    }

    @Override
    public List<Reminder> findAllUserReminders(Long chatId) {
        return reminderRepository.findByChatId(chatId);
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
    @Transactional
    public int deleteDeliveredReminders() {
        return reminderRepository.deleteByDelivered(true);
    }

    @Override
    public void updateReminderDelivered(Reminder reminder, boolean delivered) {
        reminder.setDelivered(delivered);
        reminderRepository.save(reminder);
    }
}
