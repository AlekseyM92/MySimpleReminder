package com.amikhaylov.mysimplereminder.database.service;

import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.time.LocalDate;
import java.util.List;

public interface RepositoryVoiceFile {
    public List<InputFile> getVoicesByChatId(Long chatId);

    public InputFile getVoice(Long reminderId);

    public List<InputFile> getUserVoicesByDate(Long chatId, LocalDate date);
}
