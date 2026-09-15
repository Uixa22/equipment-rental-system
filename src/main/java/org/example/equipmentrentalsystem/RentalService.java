package org.example.equipmentrentalsystem;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RentalService {
    private final Map<Long, Rental> rentalMap= new HashMap<>();
    private final AtomicLong idCounter=new AtomicLong();

    public Rental getEquipmentById(Long id) {
        if(!rentalMap.containsKey(id)){
            throw new IllegalArgumentException("No such id not found");
        }
        return rentalMap.get(id);
    }

    public List<Rental> getAllEquipment() {
        return rentalMap.values().stream().toList();
    }

    public Rental createEquipmentRental(Rental rental) {
        if(rental.id()!=null){
            throw new IllegalArgumentException("Id should be empty");
        }
        if(rental.status()!=null){
            throw new IllegalArgumentException("Status should be REQUESTED");
        }
        var createEquipment=new Rental(
                idCounter.incrementAndGet(),
                rental.userId(),
                rental.equipment(),
                rental.startDate(),
                rental.endDate(),
                RentalStatus.REQUESTED
        );
        rentalMap.put(createEquipment.id(),createEquipment);
        return createEquipment;
    }

    public Rental updateRental(Long id, Rental rental) {
        if(!rentalMap.containsKey(id)){
            throw new IllegalArgumentException("No such id not found");
        }
        var oldRental=rentalMap.get(id);
        var newRental=new Rental(
                oldRental.id(),
                rental.userId(),
                rental.equipment(),
                rental.startDate(),
                rental.endDate(),
                oldRental.status()
        );
        rentalMap.put(oldRental.id(), newRental);
        return newRental;
    }

    public Rental deleteRental(Long id) {
        if(rentalMap.containsKey(id)){
            throw new IllegalArgumentException("Id not found");
        }
         return rentalMap.remove(id);
    }

    public Rental approveRental(Long id) {
        var rental= rentalMap.get(id);
        if(!rentalMap.containsKey(id)){
            throw new IllegalArgumentException("No such id not found");
        }
        if (rental.status()!=RentalStatus.REQUESTED){
            throw new IllegalArgumentException("Status should be REQUESTED");
        }
        if (isConflict(rental)){
            throw new IllegalArgumentException("This rental has a conflict");
        }
            var newRental=new Rental(
                    rental.id(),
                    rental.userId(),
                    rental.equipment(),
                    rental.startDate(),
                    rental.endDate(),
                    RentalStatus.APPROVED
            );
            rentalMap.put(newRental.id(),newRental);

        return newRental;
    }

    private boolean isConflict(Rental rental) {
         return rentalMap.values().stream()
                 .filter(r->!r.id().equals(rental.id()))
                .filter(r->r.equipment().equals(rental.equipment()))
                 .filter(r->r.status()== RentalStatus.APPROVED)
                .anyMatch(r->r.startDate().isBefore(rental.endDate())&& rental.startDate().isBefore(r.endDate()));
    }

}
