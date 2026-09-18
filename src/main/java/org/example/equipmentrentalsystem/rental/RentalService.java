package org.example.equipmentrentalsystem.rental;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;


@Service
@Slf4j
public class RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper mapper;

    public RentalService(RentalRepository rentalRepository, RentalMapper mapper) {
        this.rentalRepository = rentalRepository;
        this.mapper = mapper;
    }

    public Rental getEquipmentById(Long id) {
        var rent = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such id not found"));
        return mapper.toDomain(rent);
    }


    public List<Rental> getAllEquipment() {
        var listRental = rentalRepository.findAll();
        return listRental.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Rental createEquipmentRental(Rental rental) {
        if (rental.status() != null) {
            throw new IllegalArgumentException("Status should be REQUESTED");
        }
        if (!rental.startDate().isBefore(rental.endDate())) {
            throw new IllegalArgumentException("Start date should be before End date");
        }

        var createEquipment = mapper.toEntity(rental);
        createEquipment.setId(null);
        createEquipment.setStatus(RentalStatus.REQUESTED);
        return mapper.toDomain(rentalRepository.save(createEquipment));
    }

    public Rental updateRental(Long id, Rental rental) {
        var oldRental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such id not found"));

        if (!rental.startDate().isBefore(rental.endDate())) {
            throw new IllegalArgumentException("Start date should be before End date");
        }

        var newRental = mapper.toEntity(rental);
        newRental.setId(oldRental.getId());
        newRental.setStatus(oldRental.getStatus());

        return mapper.toDomain(rentalRepository.save(newRental));
    }

    @Transactional
    public void rejectRental(Long id, RentalStatus status) {

        var rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found rental with id: " + id));
        if (rental.getStatus().equals(RentalStatus.APPROVED)) {
            throw new IllegalStateException("Cannot canceled rental with sate APPROVED, Contact with manager");
        }
        if (rental.getStatus().equals(RentalStatus.REJECTED)) {
            throw new IllegalStateException("Cannot canceled reservation with sate CANCELLED, ");
        }

        rentalRepository.setStatus(id, status);


    }

    public Rental approveRental(Long id) {
        var rental = rentalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No such id not found"));

        if (rental.getStatus() != RentalStatus.REQUESTED) {
            throw new IllegalArgumentException("Status should be REQUESTED");
        }
        if (isConflict(
                rental.getEquipment(),
                rental.getStartDate(),
                rental.getEndDate()
        )) {
            throw new IllegalArgumentException("This rental has a conflict");
        }

        rental.setStatus(RentalStatus.APPROVED);
        return mapper.toDomain(rentalRepository.save(rental));

    }


    private boolean isConflict(String equipment, LocalDate startDate, LocalDate endDate) {

        List<Long> conflictIds = rentalRepository.findConflict(equipment, startDate, endDate);
        if (conflictIds.isEmpty()) {
            return false;
        }
        log.info("conflict with ids:" + conflictIds);
        return true;

    }
}
