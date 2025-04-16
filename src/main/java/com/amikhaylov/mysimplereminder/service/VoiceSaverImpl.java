package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Service
public class VoiceSaverImpl implements VoiceSaver {
    private final String filepath;

    public VoiceSaverImpl(@Value("${srb.voice.filepath}") String filepath) {
        this.filepath = filepath;
    }

    @Override
    public String downloadAndSaveVoiceFile(Message message, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        var tempFilePath = simpleReminderBot.execute(new GetFile(message.getVoice().getFileId()));
        var resultFilePath = filepath + Long.toString(message.getChatId())
                + "_" + message.getMessageId()
                + "_" + message.getChat().getFirstName()
                + "-" + message.getChat().getLastName()
                + ".ogg";
        simpleReminderBot.downloadFile(tempFilePath, new File(resultFilePath));
        return resultFilePath;
    }

    @Override
    public String downloadAndSaveVoiceFile(Message message, String fileName, SimpleReminderBot simpleReminderBot)
            throws TelegramApiException {
        var tempFilePath = simpleReminderBot.execute(new GetFile(message.getVoice().getFileId()));
        var resultFilePath = filepath + fileName;
        simpleReminderBot.downloadFile(tempFilePath, new File(filepath + fileName));
        return resultFilePath;
    }
}
