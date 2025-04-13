package com.amikhaylov.mysimplereminder.keyboards;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReminderInlineButtons {
    private Map<String, InlineKeyboardButton> buttonMap;

    public ReminderInlineButtons() {
        buttonMap = new HashMap<>();
        addButton("cancel", InlineKeyboardButton.builder().text("Отмена").callbackData("cancel").build());
        addButton("next_step", InlineKeyboardButton.builder().text("Далее").callbackData("next_step").build());
        addButton("january", InlineKeyboardButton.builder().text("Январь").callbackData("january").build());
        addButton("february", InlineKeyboardButton.builder().text("Февраль").callbackData("february").build());
        addButton("march", InlineKeyboardButton.builder().text("Март").callbackData("march").build());
        addButton("april", InlineKeyboardButton.builder().text("Апрель").callbackData("april").build());
        addButton("may", InlineKeyboardButton.builder().text("Май").callbackData("may").build());
        addButton("june", InlineKeyboardButton.builder().text("Июнь").callbackData("june").build());
        addButton("july", InlineKeyboardButton.builder().text("Июль").callbackData("july").build());
        addButton("august", InlineKeyboardButton.builder().text("Август").callbackData("august").build());
        addButton("september", InlineKeyboardButton.builder().text("Сентябрь").callbackData("september").build());
        addButton("october", InlineKeyboardButton.builder().text("Октябрь").callbackData("october").build());
        addButton("november", InlineKeyboardButton.builder().text("Ноябрь").callbackData("november").build());
        addButton("december", InlineKeyboardButton.builder().text("Декабрь").callbackData("december").build());
        addButton("1", InlineKeyboardButton.builder().text("1").callbackData("1").build());
        addButton("2", InlineKeyboardButton.builder().text("2").callbackData("2").build());
        addButton("3", InlineKeyboardButton.builder().text("3").callbackData("3").build());
        addButton("4", InlineKeyboardButton.builder().text("4").callbackData("4").build());
        addButton("5", InlineKeyboardButton.builder().text("5").callbackData("5").build());
        addButton("6", InlineKeyboardButton.builder().text("6").callbackData("6").build());
        addButton("7", InlineKeyboardButton.builder().text("7").callbackData("7").build());
        addButton("8", InlineKeyboardButton.builder().text("8").callbackData("8").build());
        addButton("9", InlineKeyboardButton.builder().text("9").callbackData("9").build());
        addButton("10", InlineKeyboardButton.builder().text("10").callbackData("10").build());
        addButton("11", InlineKeyboardButton.builder().text("11").callbackData("11").build());
        addButton("12", InlineKeyboardButton.builder().text("12").callbackData("12").build());
        addButton("13", InlineKeyboardButton.builder().text("13").callbackData("13").build());
        addButton("14", InlineKeyboardButton.builder().text("14").callbackData("14").build());
        addButton("15", InlineKeyboardButton.builder().text("15").callbackData("15").build());
        addButton("16", InlineKeyboardButton.builder().text("16").callbackData("16").build());
        addButton("17", InlineKeyboardButton.builder().text("17").callbackData("17").build());
        addButton("18", InlineKeyboardButton.builder().text("18").callbackData("18").build());
        addButton("19", InlineKeyboardButton.builder().text("19").callbackData("19").build());
        addButton("20", InlineKeyboardButton.builder().text("20").callbackData("20").build());
        addButton("21", InlineKeyboardButton.builder().text("21").callbackData("21").build());
        addButton("22", InlineKeyboardButton.builder().text("22").callbackData("22").build());
        addButton("23", InlineKeyboardButton.builder().text("23").callbackData("23").build());
        addButton("24", InlineKeyboardButton.builder().text("24").callbackData("24").build());
        addButton("25", InlineKeyboardButton.builder().text("25").callbackData("25").build());
        addButton("26", InlineKeyboardButton.builder().text("26").callbackData("26").build());
        addButton("27", InlineKeyboardButton.builder().text("27").callbackData("27").build());
        addButton("28", InlineKeyboardButton.builder().text("28").callbackData("28").build());
        addButton("29", InlineKeyboardButton.builder().text("29").callbackData("29").build());
        addButton("30", InlineKeyboardButton.builder().text("30").callbackData("30").build());
        addButton("31", InlineKeyboardButton.builder().text("31").callbackData("31").build());
        addButton("done", InlineKeyboardButton.builder().text("Готово").callbackData("done").build());
        addButton("to_choose_days", InlineKeyboardButton.builder().text("Далее")
                .callbackData("to_choose_days").build());
        addButton("finish", InlineKeyboardButton.builder().text("Готово").callbackData("finish").build());
        addButton("prev_step", InlineKeyboardButton.builder().text("Назад")
                .callbackData("prev_step").build());
    }

    public void addButton(String callbackData, InlineKeyboardButton button) {
        buttonMap.put(callbackData, button);
    }

    public List<InlineKeyboardButton> getButtons() {
        return buttonMap.values().stream().toList();
    }

    public Map<String, InlineKeyboardButton> getButtonMap() {
        return buttonMap;
    }

    public InlineKeyboardButton getButton(String callbackData) {
        return buttonMap.get(callbackData);
    }
}