package com.amikhaylov.mysimplereminder.service;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
@Log4j
public class TextSaverImpl implements TextSaver {
    private final String filepath;

    public TextSaverImpl(@Value("${srb.text.filepath}") String filepath) {
        this.filepath = filepath;
    }

    @Override
    public String saveTextFile(Message message) {
        String resultPath = this.filepath + Long.toString(message.getChatId())
                + "_" + message.getMessageId()
                + "_" + message.getChat().getFirstName()
                + "-" + message.getChat().getLastName()
                + ".txt";
        File file = new File(resultPath);
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(message.getText());
            fileWriter.flush();
            return resultPath;
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public String saveTextFile(Message message, String fileName) {
        String resultPath = this.filepath + fileName;
        File file = new File(resultPath);
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(message.getText());
            fileWriter.flush();
            return resultPath;
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }
}
