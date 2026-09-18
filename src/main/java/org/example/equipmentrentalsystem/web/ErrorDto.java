package org.example.equipmentrentalsystem.web;

import java.time.LocalDateTime;

public record ErrorDto(
        String message,
        String detailMessage,
        LocalDateTime errorTime
) {
}
