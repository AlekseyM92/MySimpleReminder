package com.amikhaylov.mysimplereminder.cache;

import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Component
@NoArgsConstructor
public class UserDataCache {
    private final Map<Long, Message> userErrorMessage = new HashMap<>();
    private final Map<Long, Message> lastBotMessage = new HashMap<>();
    private final Map<Long, BotStatus> userStateData = new HashMap<>();
    private final Map<Long, String> userChoiceOfMonth = new HashMap<>();
    private final Map<Long, String> userChoiceOfDay = new HashMap<>();
    private final Map<Long, Message> reminderVoiceMessage = new HashMap<>();
    private final Map<Long, Message> reminderTextMessage = new HashMap<>();
    private final Map<Long, Integer> reminderYear = new HashMap<>();
    private final Map<Long, String> userName = new HashMap<>();
    private final Map<Long, List<Reminder>> userReminders = new HashMap<>();
    private final Map<Long, List<Map<Long, Message>>> tempUserMessages = new HashMap<>();
    private final Map<Long, Reminder> editReminder = new HashMap<>();

    public BotStatus getUserState(Long chatId) {
        if (userStateData.containsKey(chatId)) {
            return userStateData.get(chatId);
        } else {
            setUserState(chatId, BotStatus.DEFAULT);
            return BotStatus.DEFAULT;
        }
    }

    public void resetUserCache(Long chatId) {
        setUserState(chatId, BotStatus.DEFAULT);
        deleteUserName(chatId);
        deleteUserChoiceOfMonth(chatId);
        deleteUserChoiceOfDay(chatId);
        deleteReminderVoiceMessage(chatId);
        deleteReminderTextMessage(chatId);
        deleteReminderYear(chatId);
        deleteLastBotMessage(chatId);
        deleteUserReminders(chatId);
        deleteTempUserMessages(chatId);
        deleteEditReminder(chatId);
    }

    public void setEditReminder(Long chatId, Reminder reminder) {
        editReminder.put(chatId, reminder);
    }

    public Reminder getEditReminder(Long chatId) {
        return editReminder.get(chatId);
    }

    public void deleteEditReminder(Long chatId) {
        editReminder.remove(chatId);
    }

    public List<Map<Long, Message>> getTempUserMessages(Long chatId) {
        return tempUserMessages.getOrDefault(chatId, Collections.emptyList());
    }

    public void setTempUserMessages(Long chatId, List<Map<Long, Message>> messages) {
        tempUserMessages.put(chatId, messages);
    }

    public void deleteTempUserMessages(Long chatId) {
        if (tempUserMessages.containsKey(chatId)) {
            tempUserMessages.remove(chatId);
        }
    }

    public List<Reminder> getUserReminders(Long chatId) {
        if (userReminders.containsKey(chatId)) {
            return userReminders.get(chatId);
        } else {
            return new ArrayList<>();
        }
    }

    public void setUserReminders(Long chatId, List<Reminder> reminders) {
        userReminders.put(chatId, reminders);
    }

    public void deleteUserReminders(Long chatId) {
        if (userReminders.containsKey(chatId)) {
            userReminders.remove(chatId);
        }
    }

    public Message getlastBotMessage(Long chatId) {
        return lastBotMessage.get(chatId);
    }

    public boolean lastBotMessageIsPresent(Long chatId) {
        return lastBotMessage.containsKey(chatId);
    }

    public void setLastBotMessage(Message message) {
        lastBotMessage.put(message.getChatId(), message);
    }

    public void deleteLastBotMessage(Long chatId) {
        lastBotMessage.remove(chatId);
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
