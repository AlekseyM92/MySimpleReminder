package com.amikhaylov.mysimplereminder.keyboards;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Getter
public class ReminderInlineButtons {
    private List<InlineKeyboardButton> buttons;
    public ReminderInlineButtons() {
        buttons = new ArrayList<>();
        addButton(InlineKeyboardButton.builder().text("Отмена").callbackData("cancel").build());
        addButton(InlineKeyboardButton.builder().text("Далее").callbackData("next_step").build());
    }
    public void addButton(InlineKeyboardButton button) {
        buttons.add(button);
    }
}