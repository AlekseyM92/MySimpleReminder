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
    @Column(name = "reminder_id")
    private Long messageId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "send_date")
    private LocalDateTime sendDateTime;

    @Column(name = "reminder_date")
    private LocalDate dateReminder;

    @Column(name = "filePath")
    private String filePath;

    @Column(name = "delivered_flag")
    private boolean isDelivered;
}
