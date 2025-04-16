package com.amikhaylov.mysimplereminder.database.service;

import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryVoiceFileImpl implements RepositoryVoiceFile {
    private final ReminderRepositoryService reminderRepositoryService;

    public RepositoryVoiceFileImpl(ReminderRepositoryService reminderRepositoryService) {
        this.reminderRepositoryService = reminderRepositoryService;
    }

    @Override
    public List<InputFile> getVoicesByChatId(Long chatId) {
        List<Reminder> reminders = reminderRepositoryService.findAllUserReminders(chatId).stream()
                .filter(reminder -> reminder.getFilePath().endsWith(".ogg"))
                .toList();
        List<InputFile> voices = new ArrayList<>();
        for (Reminder reminder : reminders) {
            voices.add(new InputFile(new File(reminder.getFilePath())));
        }
        return voices;
    }

    @Override
    public InputFile getVoice(Long reminderId) {
        Reminder reminder = reminderRepositoryService.getReminder(reminderId);
        if (reminder != null && reminder.getFilePath().endsWith(".ogg")) {
            return new InputFile(new File(reminder.getFilePath()));
        }
        return null;
    }

    @Override
    public List<InputFile> getUserVoicesByDate(Long chatId, LocalDate date) {
        List<Reminder> reminders = reminderRepositoryService.findAllUserReminders(chatId, date).stream()
                .filter(reminder -> reminder.getFilePath().endsWith(".ogg"))
                .toList();
        List<InputFile> voices = new ArrayList<>();
        for (Reminder reminder : reminders) {
            voices.add(new InputFile(new File(reminder.getFilePath())));
        }
        return voices;
    }
}
