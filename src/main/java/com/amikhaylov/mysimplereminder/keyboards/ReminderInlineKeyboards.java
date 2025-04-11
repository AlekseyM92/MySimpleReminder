package com.amikhaylov.mysimplereminder.keyboards;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
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
        LocalDate date = LocalDate.now();
        int currentMonth = date.getMonth().getValue();
        InlineKeyboardMarkup inlineKeyboardMarkupMonths = new InlineKeyboardMarkup();
        InlineKeyboardMarkup inlineKeyboardMarkupDays;
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow4;
        List<List<InlineKeyboardButton>> keyboardMonths = new ArrayList<>();
        List<List<InlineKeyboardButton>> keyboardDays = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            keyboardRow1.add(this.reminderInlineButtons
                    .getButton(Month.of(currentMonth + i).toString().toLowerCase()));
            keyboardRow2.add(this.reminderInlineButtons
                    .getButton(Month.of(currentMonth + i + 3).toString().toLowerCase()));
        }
        keyboardRow3.add(this.reminderInlineButtons.getButton("cancel"));
        keyboardRow3.add(this.reminderInlineButtons.getButton("to_choose_days"));
        keyboardMonths.add(keyboardRow1);
        keyboardMonths.add(keyboardRow2);
        keyboardMonths.add(keyboardRow3);
        inlineKeyboardMarkupMonths.setKeyboard(keyboardMonths);
        addKeyboard("months", inlineKeyboardMarkupMonths);
        log.info("addKeyboard \"months\"");

        for (int i = 0; i < 1; i++) {
            inlineKeyboardMarkupDays = new InlineKeyboardMarkup();
            int valueCurrentMonth = currentMonth + i;
            int minDay;
            int maxDay;
            int maxRows;
            if (i == 0) {
                minDay = date.getDayOfMonth() + 1;
                maxDay = date.lengthOfMonth();
                maxRows = (int) Math
                        .ceil((double) (maxDay - minDay) / 7);
            } else {
                minDay = 1;
                maxDay = Month.of(valueCurrentMonth).maxLength();
                maxRows = (int) Math
                        .ceil((double) (maxDay) / 7);
            }

            for (int j = 0; j < maxRows; j++) {
                keyboardRow4 = new ArrayList<>();
                for (int k = minDay; k < minDay + 8; k++) {
                    keyboardRow4.add(reminderInlineButtons.getButton(Integer.toString(k)));
                    if (k == maxDay) {
                        break;
                    }
                }
                minDay += 8;
                keyboardDays.add(keyboardRow4);
            }
            inlineKeyboardMarkupDays.setKeyboard(keyboardDays);
            addKeyboard(Month.of(currentMonth).toString().toLowerCase(), inlineKeyboardMarkupDays);
            log.info("addKeyboard " + Month.of(valueCurrentMonth).toString().toLowerCase());
        }

        log.info(keyboards);
    }
}
