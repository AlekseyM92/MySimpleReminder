package com.amikhaylov.mysimplereminder.cache;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;

@Component
@NoArgsConstructor
public class UserDataCache {
    private static Map<Chat, Message> userErrorMessage = new HashMap<>();
    private static Map<Long, BotStatus> userStateData = new HashMap<>();
    public BotStatus getUserState(Long chatId) {
        if (userStateData.containsKey(chatId)) {
            return userStateData.get(chatId);
        } else {
            setUserState(chatId, BotStatus.DEFAULT);
            return BotStatus.DEFAULT;
        }
    }

    public Message getUserErrorMessage(Chat chat) {
        if (userErrorMessage.containsKey(chat)) {
            return userErrorMessage.get(chat);
        }
        return null;
    }

    public boolean errorMessageIsPresent(Chat chat) {
        return userErrorMessage.containsKey(chat);
    }

    public void setUserState(Long chatId, BotStatus botStatus) {
        userStateData.put(chatId, botStatus);
    }

    public void setUserErrorMessage(Message message) {
        userErrorMessage.put(message.getChat(), message);
    }

    public void deleteUserErrorMessage(Chat chat) {
        userErrorMessage.remove(chat);
    }
}
