package com.amikhaylov.mysimplereminder.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface TextSaver {
    public String saveTextFile(Message message);
    public String saveTextFile(Message message, final String fileName);
}
