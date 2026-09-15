package org.example.equipmentrentalsystem;

import java.time.LocalDate;

public record Rental(
        Long id,
        Long userId,
        String equipment,
        LocalDate startDate,
        LocalDate endDate,
        RentalStatus status
) {
}
