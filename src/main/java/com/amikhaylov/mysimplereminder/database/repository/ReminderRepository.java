package com.amikhaylov.mysimplereminder.database.repository;

import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findAllByChatId(Long chatId);

    void deleteAllByChatId(Long chatId);

    List<Reminder> findAllByChatIdAndDateReminder(Long chatId, LocalDate dateReminder);

    List<Reminder> findAllByDateReminderAndDeliveredIs(LocalDate dateReminder, boolean delivered);

    void deleteAllByDeliveredIs(boolean delivered);
}
