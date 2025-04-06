package com.amikhaylov.mysimplereminder.keyboards;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Getter
public class ReminderInlineKeyboards {
    private Map<String, InlineKeyboardMarkup> keyboards;
    private ReminderInlineButtons reminderInlineButtons;
    @Autowired
    public ReminderInlineKeyboards(ReminderInlineButtons reminderInlineButtons) {
        this.reminderInlineButtons = reminderInlineButtons;
        this.keyboards = new HashMap<>();
        addKeyboard("/create_reminder_1", InlineKeyboardMarkup.builder()
                .keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("cancel")
                                        || button.getCallbackData().equals("next_step"))
                        .collect(Collectors.toList())
                ).build()
        );
    }
    public void addKeyboard(String nameOfKeyboard, InlineKeyboardMarkup keyboard) {
        keyboards.put(nameOfKeyboard, keyboard);
    }
    public InlineKeyboardMarkup getKeyboard(String nameOfKeyboard) {
        return keyboards.get(nameOfKeyboard);
    }
}
