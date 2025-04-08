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
        addButton(InlineKeyboardButton.builder().text("Январь").callbackData("january").build());
        addButton(InlineKeyboardButton.builder().text("Февраль").callbackData("february").build());
        addButton(InlineKeyboardButton.builder().text("Март").callbackData("march").build());
        addButton(InlineKeyboardButton.builder().text("Апрель").callbackData("april").build());
        addButton(InlineKeyboardButton.builder().text("Май").callbackData("may").build());
        addButton(InlineKeyboardButton.builder().text("Июнь").callbackData("june").build());
        addButton(InlineKeyboardButton.builder().text("Июль").callbackData("july").build());
        addButton(InlineKeyboardButton.builder().text("Август").callbackData("august").build());
        addButton(InlineKeyboardButton.builder().text("Сентябрь").callbackData("september").build());
        addButton(InlineKeyboardButton.builder().text("Октябрь").callbackData("october").build());
        addButton(InlineKeyboardButton.builder().text("Ноябрь").callbackData("november").build());
        addButton(InlineKeyboardButton.builder().text("Декабрь").callbackData("december").build());
        addButton(InlineKeyboardButton.builder().text("1").callbackData("1").build());
        addButton(InlineKeyboardButton.builder().text("2").callbackData("2").build());
        addButton(InlineKeyboardButton.builder().text("3").callbackData("3").build());
        addButton(InlineKeyboardButton.builder().text("4").callbackData("4").build());
        addButton(InlineKeyboardButton.builder().text("5").callbackData("5").build());
        addButton(InlineKeyboardButton.builder().text("6").callbackData("6").build());
        addButton(InlineKeyboardButton.builder().text("7").callbackData("7").build());
        addButton(InlineKeyboardButton.builder().text("8").callbackData("8").build());
        addButton(InlineKeyboardButton.builder().text("9").callbackData("9").build());
        addButton(InlineKeyboardButton.builder().text("10").callbackData("10").build());
        addButton(InlineKeyboardButton.builder().text("11").callbackData("11").build());
        addButton(InlineKeyboardButton.builder().text("12").callbackData("12").build());
        addButton(InlineKeyboardButton.builder().text("13").callbackData("13").build());
        addButton(InlineKeyboardButton.builder().text("14").callbackData("14").build());
        addButton(InlineKeyboardButton.builder().text("15").callbackData("15").build());
        addButton(InlineKeyboardButton.builder().text("16").callbackData("16").build());
        addButton(InlineKeyboardButton.builder().text("17").callbackData("17").build());
        addButton(InlineKeyboardButton.builder().text("18").callbackData("18").build());
        addButton(InlineKeyboardButton.builder().text("19").callbackData("19").build());
        addButton(InlineKeyboardButton.builder().text("20").callbackData("20").build());
        addButton(InlineKeyboardButton.builder().text("21").callbackData("21").build());
        addButton(InlineKeyboardButton.builder().text("22").callbackData("22").build());
        addButton(InlineKeyboardButton.builder().text("23").callbackData("23").build());
        addButton(InlineKeyboardButton.builder().text("24").callbackData("24").build());
        addButton(InlineKeyboardButton.builder().text("25").callbackData("25").build());
        addButton(InlineKeyboardButton.builder().text("26").callbackData("26").build());
        addButton(InlineKeyboardButton.builder().text("27").callbackData("27").build());
        addButton(InlineKeyboardButton.builder().text("28").callbackData("28").build());
        addButton(InlineKeyboardButton.builder().text("29").callbackData("29").build());
        addButton(InlineKeyboardButton.builder().text("30").callbackData("30").build());
        addButton(InlineKeyboardButton.builder().text("31").callbackData("31").build());
        addButton(InlineKeyboardButton.builder().text("Готово").callbackData("done").build());
    }
    public void addButton(InlineKeyboardButton button) {
        buttons.add(button);
    }
}