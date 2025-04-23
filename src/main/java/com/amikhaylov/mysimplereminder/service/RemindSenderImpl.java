package com.amikhaylov.mysimplereminder.service;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import com.amikhaylov.mysimplereminder.database.service.ReminderRepositoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;

@Component
@RequiredArgsConstructor
@Getter
public class RemindSenderImpl implements RemindSender {
    private final ReminderRepositoryService reminderRepositoryService;

    @Override
    public void sendReminder(Reminder reminder, SimpleReminderBot simpleReminderBot) throws TelegramApiException {
        simpleReminderBot.execute(SendMessage.builder()
                .chatId(reminder.getChatId())
                .text("Внимание! Вам напоминание: \n")
                .build()
        );
        if (reminder.getFilePath() != null && reminder.getFilePath().endsWith(".txt")) {
            String str1 = "";
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(reminder.getFilePath()))) {
                while (bufferedReader.ready()) {
                    str1 = str1 + bufferedReader.readLine();
                }
                simpleReminderBot.execute(SendMessage.builder()
                        .chatId(reminder.getChatId())
                        .text(str1)
                        .build()
                );
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (reminder.getFilePath() != null && reminder.getFilePath().endsWith(".ogg")) {
            InputFile inputFile = new InputFile(new File(reminder.getFilePath()));
            SendVoice sendVoice = SendVoice.builder()
                    .voice(inputFile)
                    .chatId(reminder.getChatId())
                    .build();
            simpleReminderBot.execute(sendVoice);
        }
    }
}
