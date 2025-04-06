package com.amikhaylov.mysimplereminder.cache;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@NoArgsConstructor
public class UserDataCache {
    private static Map<Long, BotStatus> userStateData = new HashMap<>();
    public BotStatus getUserState(Long chatId) {
        if (userStateData.containsKey(chatId)) {
            return userStateData.get(chatId);
        } else {
            setUserState(chatId, BotStatus.DEFAULT);
            return BotStatus.DEFAULT;
        }
    }
    public void setUserState(Long chatId, BotStatus botStatus) {
        userStateData.put(chatId, botStatus);
    }
}
