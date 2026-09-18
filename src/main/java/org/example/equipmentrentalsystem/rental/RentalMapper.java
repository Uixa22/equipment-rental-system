package org.example.equipmentrentalsystem.rental;

import org.springframework.stereotype.Component;

@Component
public class RentalMapper {
    public Rental toDomain(RentalEntity rentalEntity) {
        return new Rental(
                rentalEntity.getId(),
                rentalEntity.getUserId(),
                rentalEntity.getEquipment(),
                rentalEntity.getStartDate(),
                rentalEntity.getEndDate(),
                rentalEntity.getStatus()
        );
    }
    public RentalEntity toEntity(Rental rental) {
        return new RentalEntity(
                rental.id(),
                rental.userId(),
                rental.equipment(),
                rental.startDate(),
                rental.endDate(),
                rental.status()
        );
    }
}
