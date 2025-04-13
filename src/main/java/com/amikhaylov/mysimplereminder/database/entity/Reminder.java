package com.amikhaylov.mysimplereminder.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminder")
@NoArgsConstructor
@Getter
@Setter
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_id", nullable = false)
    private Long messageId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "send_date", nullable = false)
    private LocalDateTime sendDateTime;

    @Column(name = "reminder_date", nullable = false)
    private LocalDate dateReminder;

    @Column(name = "filePath", nullable = false)
    private String filePath;

    @Column(name = "delivered_flag")
    private boolean delivered;

    public Reminder(Long chatId, String userName, LocalDateTime sendDateTime, LocalDate dateReminder, String filePath, boolean delivered) {
        this.chatId = chatId;
        this.userName = userName;
        this.sendDateTime = sendDateTime;
        this.dateReminder = dateReminder;
        this.filePath = filePath;
        this.delivered = delivered;
    }
}
