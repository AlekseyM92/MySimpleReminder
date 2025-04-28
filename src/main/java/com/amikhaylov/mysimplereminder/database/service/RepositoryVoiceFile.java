package com.amikhaylov.mysimplereminder.database.service;

import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.time.LocalDate;
import java.util.List;

public interface
RepositoryVoiceFile {
    public List<InputFile> getVoicesByChatId(Long chatId);

    public InputFile getVoice(Reminder reminder);

    public List<InputFile> getUserVoicesByDate(Long chatId, LocalDate date);
}
