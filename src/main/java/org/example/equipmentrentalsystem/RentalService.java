package org.example.equipmentrentalsystem;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class RentalService {
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental getEquipmentById(Long id) {
        var rent = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such id not found"));
        return domainRental(rent);
    }


    public List<Rental> getAllEquipment() {
        var listRental = rentalRepository.findAll();
        return listRental.stream()
                .map(this::domainRental)
                .toList();
    }

    public Rental createEquipmentRental(Rental rental) {
        if (rental.status() != null) {
            throw new IllegalArgumentException("Status should be REQUESTED");
        }
        if(!rental.startDate().isBefore(rental.endDate())){
            throw new IllegalArgumentException("Start date should be before End date");
        }
        RentalEntity createEquipment = new RentalEntity(
                null,
                rental.userId(),
                rental.equipment(),
                rental.startDate(),
                rental.endDate(),
                RentalStatus.REQUESTED
        );
        return domainRental(rentalRepository.save(createEquipment));
    }

    public Rental updateRental(Long id, Rental rental) {
        var oldRental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such id not found"));

        if(!rental.startDate().isBefore(rental.endDate())){
            throw new IllegalArgumentException("Start date should be before End date");
        }
        var newRental = new RentalEntity(
                oldRental.getId(),
                rental.userId(),
                rental.equipment(),
                rental.startDate(),
                rental.endDate(),
                oldRental.getStatus()
        );
        return domainRental(rentalRepository.save(newRental));
    }
    @Transactional
    public void rejectRental(Long id,RentalStatus status) {

        var rental = rentalRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found rental with id: "+ id));
        if (rental.getStatus().equals(RentalStatus.APPROVED)){
            throw new IllegalStateException("Cannot canceled rental with sate APPROVED, Contact with manager");
        }
        if (rental.getStatus().equals(RentalStatus.REJECTED)){
            throw new IllegalStateException("Cannot canceled reservation with sate CANCELLED, ");
        }

        rentalRepository.setStatus(id,status);


    }

    public Rental approveRental(Long id) {
        var rental = rentalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No such id not found"));

        if (rental.getStatus() != RentalStatus.REQUESTED) {
            throw new IllegalArgumentException("Status should be REQUESTED");
        }
        if (isConflict(rental)) {
            throw new IllegalArgumentException("This rental has a conflict");
        }
        var newRental = new RentalEntity(
                rental.getId(),
                rental.getUserId(),
                rental.getEquipment(),
                rental.getStartDate(),
                rental.getEndDate(),
                RentalStatus.APPROVED
        );
        return domainRental(rentalRepository.save(newRental));

    }


    private boolean isConflict(RentalEntity rental) {
         return rentalRepository.findAll().stream()
                 .filter(r->!r.getId().equals(rental.getId()))
                 .filter(r->r.getEquipment().equals(rental.getEquipment()))
                 .filter(r->r.getStatus()== RentalStatus.APPROVED)
                 .anyMatch(r->r.getStartDate().isBefore(rental.getEndDate())&& rental.getStartDate().isBefore(r.getEndDate()));

    }

    private Rental domainRental(RentalEntity rentalEntity) {
        return new Rental(
                rentalEntity.getId(),
                rentalEntity.getUserId(),
                rentalEntity.getEquipment(),
                rentalEntity.getStartDate(),
                rentalEntity.getEndDate(),
                rentalEntity.getStatus()
        );
    }


}
