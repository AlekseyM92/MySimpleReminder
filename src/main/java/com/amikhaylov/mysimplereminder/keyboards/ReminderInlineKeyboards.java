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
        addKeyboard("30_days", InlineKeyboardMarkup.builder()
                .keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("1") ||
                                        button.getCallbackData().equals("2") ||
                                        button.getCallbackData().equals("3") ||
                                        button.getCallbackData().equals("4") ||
                                        button.getCallbackData().equals("5") ||
                                        button.getCallbackData().equals("6") ||
                                        button.getCallbackData().equals("7")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("8") ||
                                        button.getCallbackData().equals("9") ||
                                        button.getCallbackData().equals("10") ||
                                        button.getCallbackData().equals("11") ||
                                        button.getCallbackData().equals("12") ||
                                        button.getCallbackData().equals("13") ||
                                        button.getCallbackData().equals("14")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("15") ||
                                        button.getCallbackData().equals("16") ||
                                        button.getCallbackData().equals("17") ||
                                        button.getCallbackData().equals("18") ||
                                        button.getCallbackData().equals("19") ||
                                        button.getCallbackData().equals("20") ||
                                        button.getCallbackData().equals("21")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("22") ||
                                        button.getCallbackData().equals("23") ||
                                        button.getCallbackData().equals("24") ||
                                        button.getCallbackData().equals("25") ||
                                        button.getCallbackData().equals("26") ||
                                        button.getCallbackData().equals("27") ||
                                        button.getCallbackData().equals("28")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("29") ||
                                        button.getCallbackData().equals("30")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("done")
                        )
                        .collect(Collectors.toList())
                ).build()
        );
        addKeyboard("31_days", InlineKeyboardMarkup.builder()
                .keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("1") ||
                                        button.getCallbackData().equals("2") ||
                                        button.getCallbackData().equals("3") ||
                                        button.getCallbackData().equals("4") ||
                                        button.getCallbackData().equals("5") ||
                                        button.getCallbackData().equals("6") ||
                                        button.getCallbackData().equals("7")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("8") ||
                                        button.getCallbackData().equals("9") ||
                                        button.getCallbackData().equals("10") ||
                                        button.getCallbackData().equals("11") ||
                                        button.getCallbackData().equals("12") ||
                                        button.getCallbackData().equals("13") ||
                                        button.getCallbackData().equals("14")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("15") ||
                                        button.getCallbackData().equals("16") ||
                                        button.getCallbackData().equals("17") ||
                                        button.getCallbackData().equals("18") ||
                                        button.getCallbackData().equals("19") ||
                                        button.getCallbackData().equals("20") ||
                                        button.getCallbackData().equals("21")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("22") ||
                                        button.getCallbackData().equals("23") ||
                                        button.getCallbackData().equals("24") ||
                                        button.getCallbackData().equals("25") ||
                                        button.getCallbackData().equals("26") ||
                                        button.getCallbackData().equals("27") ||
                                        button.getCallbackData().equals("28")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("29") ||
                                        button.getCallbackData().equals("30") ||
                                        button.getCallbackData().equals("31")
                        )
                        .collect(Collectors.toList())
                ).keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("done")
                        )
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
