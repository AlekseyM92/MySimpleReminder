package com.amikhaylov.mysimplereminder.cache;

public enum BotStatus {
    DEFAULT,
    CREATE_REMINDER,
    WAITING_FOR_APPLY_MESSAGE_REMINDER,
    WAITING_FOR_APPLY_MONTH,
    WAITING_FOR_APPLY_DAY,
    MY_REMINDERS,
    DELETE_ALL_REMINDERS,
    LANGUAGE,
    TERMINATE_BOT;
}
