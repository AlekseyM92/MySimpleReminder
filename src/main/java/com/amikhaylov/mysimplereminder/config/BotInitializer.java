package com.amikhaylov.mysimplereminder.config;

import com.amikhaylov.mysimplereminder.controller.SimpleReminderBot;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@AllArgsConstructor
@Log4j
public class BotInitializer {
    SimpleReminderBot simpleReminderBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(simpleReminderBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

}
