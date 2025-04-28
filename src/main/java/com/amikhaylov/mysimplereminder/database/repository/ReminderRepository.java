package com.amikhaylov.mysimplereminder.database.repository;

import com.amikhaylov.mysimplereminder.database.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    void deleteAllByChatId(Long chatId);

    List<Reminder> findAllByChatIdAndDateReminder(Long chatId, LocalDate dateReminder);

    List<Reminder> findAllByDateReminderAndDeliveredIs(LocalDate dateReminder, boolean delivered);

    int deleteByDelivered(boolean delivered);

    List<Reminder> findByChatIdAndDelivered(Long chatId, boolean delivered);

    List<Reminder> findByChatId(Long chatId);
}
