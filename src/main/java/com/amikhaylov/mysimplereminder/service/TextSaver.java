package com.amikhaylov.mysimplereminder.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface TextSaver {
    public void saveTextFile(Message message);
    public void saveTextFile(Message message, final String fileName);
}
