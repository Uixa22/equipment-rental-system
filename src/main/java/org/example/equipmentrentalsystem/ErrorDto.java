package org.example.equipmentrentalsystem;

import java.time.LocalDateTime;

public record ErrorDto(
        String message,
        String detailMessage,
        LocalDateTime errorTime
) {
}
