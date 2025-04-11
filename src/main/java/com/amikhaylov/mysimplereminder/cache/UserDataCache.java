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
    private Map<Long, Message> userErrorMessage = new HashMap<>();
    private Map<Long, BotStatus> userStateData = new HashMap<>();
    private Map<Long, Integer> userChoiceOfMonth = new HashMap<>();
    private Map<Long, Integer> userChoiceOfDay = new HashMap<>();

    public BotStatus getUserState(Long chatId) {
        if (userStateData.containsKey(chatId)) {
            return userStateData.get(chatId);
        } else {
            setUserState(chatId, BotStatus.DEFAULT);
            return BotStatus.DEFAULT;
        }
    }

    public Message getUserErrorMessage(Long chatId) {
        return userErrorMessage.get(chatId);
    }

    public boolean errorMessageIsPresent(Long chatId) {
        return userErrorMessage.containsKey(chatId);
    }

    public void setUserState(Long chatId, BotStatus botStatus) {
        userStateData.put(chatId, botStatus);
    }

    public void setUserErrorMessage(Message message) {
        userErrorMessage.put(message.getChatId(), message);
    }

    public void deleteUserErrorMessage(Long chatId) {
        userErrorMessage.remove(chatId);
    }

    public void setUserChoiceOfMonth(Long chatId, Integer month) {
        userChoiceOfMonth.put(chatId, month);
    }

    public Integer getUserChoiceOfMonth(Long chatId) {
        return userChoiceOfMonth.get(chatId);
    }

    public void setUserChoiceOfDay(Long chatId, Integer day) {
        userChoiceOfDay.put(chatId, day);
    }

    public Integer getUserChoiceOfDay(Long chatId) {
        return userChoiceOfDay.get(chatId);
    }
}
