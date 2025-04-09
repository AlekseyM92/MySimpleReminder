package com.amikhaylov.mysimplereminder.keyboards;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.text.SimpleDateFormat;
import java.util.*;
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
                                button.getCallbackData().equals("cancel") ||
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
                                button.getCallbackData().equals("cancel") ||
                                        button.getCallbackData().equals("done")
                        )
                        .collect(Collectors.toList())
                ).build()
        );

        addKeyboard("months", InlineKeyboardMarkup.builder()
                .keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("january") ||
                                        button.getCallbackData().equals("february") ||
                                        button.getCallbackData().equals("march") ||
                                        button.getCallbackData().equals("april") ||
                                        button.getCallbackData().equals("may") ||
                                        button.getCallbackData().equals("june") ||
                                        button.getCallbackData().equals("july") ||
                                        button.getCallbackData().equals("august") ||
                                        button.getCallbackData().equals("september") ||
                                        button.getCallbackData().equals("october") ||
                                        button.getCallbackData().equals("november") ||
                                        button.getCallbackData().equals("december")
                        )
                        .collect(Collectors.toList())
                )
                .keyboardRow(this.reminderInlineButtons.getButtons()
                        .stream()
                        .filter(button ->
                                button.getCallbackData().equals("cancel")
                                        || button.getCallbackData().equals("to_choose_days"))
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

    public void refreshKeyboards() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            calendar.add(Calendar.MONTH, 1);
            keyboardRow1.add(this.reminderInlineButtons.getButton(dateFormat.format(calendar.getTime()).toLowerCase()));
        }
        for (int i = 0; i < 3; i++) {
            calendar.add(Calendar.MONTH, 1);
            keyboardRow2.add(this.reminderInlineButtons.getButton(dateFormat.format(calendar.getTime()).toLowerCase()));
        }
        keyboardRow3.add(this.reminderInlineButtons.getButton("cancel"));
        keyboardRow3.add(this.reminderInlineButtons.getButton("to_choose_days"));
        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);
        keyboard.add(keyboardRow3);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        addKeyboard("months", inlineKeyboardMarkup);
    }
}
