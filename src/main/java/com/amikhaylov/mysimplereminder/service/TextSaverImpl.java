package com.amikhaylov.mysimplereminder.service;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
@Log4j
public class TextSaverImpl implements TextSaver {
    private final String filepath;

    public TextSaverImpl(@Value("${srb.text.filepath}") String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void saveTextFile(Message message) {
        File file = new File(this.filepath + Long.toString(message.getChatId())
                + "_" + message.getMessageId()
                + "_" + message.getChat().getFirstName()
                + "-" + message.getChat().getLastName()
                + ".txt");
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(message.getText());
            fileWriter.flush();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void saveTextFile(Message message, String fileName) {
        File file = new File(this.filepath + fileName);
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(message.getText());
            fileWriter.flush();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
