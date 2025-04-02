package com.amikhaylov.mysimplereminder.controller;

import com.amikhaylov.mysimplereminder.config.BotConfig;
import lombok.extern.log4j.Log4j;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j
public class SimpleReminderBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public SimpleReminderBot(BotConfig botConfig) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать общение с ботом"));
        listOfCommands.add(new BotCommand("/mydata", "Получить мои персональные данные"));
        listOfCommands.add(new BotCommand("/deletedata", "Удалить мои данные"));
        listOfCommands.add(new BotCommand("/help", "Справка по управлению ботом"));
        listOfCommands.add(new BotCommand("/settings", "Настройки бота"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void onUpdateReceived(Update update) {
        var message = update.getMessage();

        if (message != null) {
            if (message.getText() != null) {
                log.debug(message.getText());
            }

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> rowButtons = new ArrayList<>();
            InlineKeyboardButton keyboardButton1 = new InlineKeyboardButton();
            InlineKeyboardButton keyboardButton2 = new InlineKeyboardButton();
            InlineKeyboardButton keyboardButton3 = new InlineKeyboardButton();
            keyboardButton1.setText("button1");
            keyboardButton1.setCallbackData("button1");
            keyboardButton2.setText("button2");
            keyboardButton2.setCallbackData("button2");
            keyboardButton3.setText("button3");
            keyboardButton3.setCallbackData("button3");
            rowButtons.add(keyboardButton1);
            rowButtons.add(keyboardButton2);
            rowButtons.add(keyboardButton3);
            rows.add(rowButtons);
            inlineKeyboardMarkup.setKeyboard(rows);

            if (message.getVoice() != null) {
                System.out.println("Voice is not null");
                var response = new SendMessage();
                var sendVoice = new SendVoice();
                var getfile  = new GetFile(message.getVoice().getFileId());

                try {
                    var filePath = execute(getfile);
                    this.downloadFile(filePath, new File("C:\\test\\new_downloaded_file.ogg"));
                } catch (TelegramApiRequestException e) {
                    throw new RuntimeException(e);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                response.setChatId(message.getChatId().toString());
                sendVoice.setChatId(message.getChatId().toString());
                //message.getVoice();
                //File file = new File();
                //sendVoice.

                sendVoice.setVoice(new InputFile(new File("C:\\test\\new_downloaded_file.ogg")));
                response.setText(message.getVoice().getMimeType() + " ------ " + sendVoice.getFile() + "  chat_id: " + sendVoice.getChatId());

//                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("C:\\test\\file_0.ogg"))) {
//                    System.out.println(bis.available());
//                    //System.out.println(fileInputStream.readAllBytes());
//                    sendVoice.setVoice(new InputFile(bis, "file_0.ogg"));
//                    sendVoice.setReplyToMessageId(message.getMessageId());
//                    response.setText(message.getVoice().getMimeType() + " ------ " + sendVoice.getFile() + "  chat_id: " + sendVoice.getChatId());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

                sendAnswerVoice(sendVoice);
                sendAnswerMessage(response);
            }

            if (message.getText() != null && !message.getText().isEmpty()) {
                var messageText = message.getText();
                var response = new SendMessage();
                response.setChatId(message.getChatId().toString());

                switch (messageText) {
                    case "/start":
                        response.setText("Привет " + message.getChat().getFirstName() + "!\n"
                                + "Вы нажали start");
                        break;
                    case "/mydata":
                        response.setText("Вы нажали mydata");
                        break;
                    case "/deletedata":
                        response.setText("Вы нажали deletedata");
                        break;
                    case "/help":
                        response.setText("Вы нажали help");
                        break;
                    case "/settings":
                        response.setText("Вы нажали settings");
                        break;
                    default:
                        response.setText("Вы ввели " + messageText);
                        break;
                }
                response.setReplyMarkup(inlineKeyboardMarkup);
                sendAnswerMessage(response);
            }
        }
    }

    private void sendAnswerVoice(SendVoice sendVoice) {
        if (sendVoice != null) {
            try {
                execute(sendVoice);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCurrentBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }
}
