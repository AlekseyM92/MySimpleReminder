package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
@Component
@NoArgsConstructor
public class VoiceHandlerImpl implements VoiceHandler {
    @Override
    public void handleVoice(Chat chat, Voice voice, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        if (chat == null) {
            throw new TelegramApiException("Chat is null");
        } else if (simpleReminderBot == null) {
            throw new TelegramApiException("SimpleReminderBot is null");
        } else if (voice == null) {
            throw new TelegramApiException("Voice is null");
        }

        Long chatId = chat.getId();
        SendVoice sendVoice = new SendVoice();
        GetFile getfile = new GetFile(voice.getFileId());
        var filePath = simpleReminderBot.execute(getfile);
        simpleReminderBot.downloadFile(filePath, new File("C:\\test\\new_downloaded_file.ogg"));
        sendVoice.setChatId(chatId.toString());
        sendVoice.setVoice(new InputFile(new File("C:\\test\\new_downloaded_file.ogg")));
        simpleReminderBot.execute(sendVoice);
    }
}
