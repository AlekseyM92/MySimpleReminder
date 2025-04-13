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
    private Map<Long, String> userChoiceOfMonth = new HashMap<>();
    private Map<Long, String> userChoiceOfDay = new HashMap<>();
    private Map<Long, Message> reminderVoiceMessage = new HashMap<>();
    private Map<Long, Message> reminderTextMessage = new HashMap<>();
    private Map<Long, Integer> reminderYear = new HashMap<>();
    private Map<Long, String> userName = new HashMap<>();

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

    public void setUserChoiceOfMonth(Long chatId, String month) {
        userChoiceOfMonth.put(chatId, month);
    }

    public String getUserChoiceOfMonth(Long chatId) {
        return userChoiceOfMonth.get(chatId);
    }

    public void setUserChoiceOfDay(Long chatId, String day) {
        userChoiceOfDay.put(chatId, day);
    }

    public String getUserChoiceOfDay(Long chatId) {
        return userChoiceOfDay.get(chatId);
    }

    public void setReminderVoiceMessage(Long chatId, Message message) {
        reminderVoiceMessage.put(chatId, message);
    }

    public Message getReminderVoiceMessage(Long chatId) {
        return reminderVoiceMessage.get(chatId);
    }

    public void setReminderTextMessage(Long chatId, Message message) {
        reminderTextMessage.put(chatId, message);
    }

    public Message getReminderTextMessage(Long chatId) {
        return reminderTextMessage.get(chatId);
    }

    public void deleteReminderVoiceMessage(Long chatId) {
        reminderVoiceMessage.remove(chatId);
    }

    public void deleteReminderTextMessage(Long chatId) {
        reminderTextMessage.remove(chatId);
    }

    public void deleteUserChoiceOfMonth(Long chatId) {
        userChoiceOfMonth.remove(chatId);
    }

    public void deleteUserChoiceOfDay(Long chatId) {
        userChoiceOfDay.remove(chatId);
    }

    public void setReminderYear(Long chatId, Integer year) {
        reminderYear.put(chatId, year);
    }

    public Integer getReminderYear(Long chatId) {
        return reminderYear.get(chatId);
    }

    public void deleteReminderYear(Long chatId) {
        reminderYear.remove(chatId);
    }

    public void setUserName(Long chatId, String userName) {
        this.userName.put(chatId, userName);
    }

    public String getUserName(Long chatId) {
        return userName.get(chatId);
    }

    public void deleteUserName(Long chatId) {
        userName.remove(chatId);
    }
}
