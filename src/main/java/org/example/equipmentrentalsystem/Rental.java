package org.example.equipmentrentalsystem;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record Rental(
        @Null
        Long id,

        @NotNull
        @Min(1)
        Long userId,

        @NotBlank
        String equipment,

        @FutureOrPresent
        @NotNull
        LocalDate startDate,
        @Future
        @NotNull
        LocalDate endDate,

        RentalStatus status
) {
}
