package org.example.equipmentrentalsystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/equipment")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService){
        this.rentalService = rentalService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable("id") Long id ){
        log.info("Call method getRentalById with id"+ id);
        return ResponseEntity.ok().body(rentalService.getEquipmentById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Rental>> getAllRental(){
        log.info("Call method getAllRental");
        return ResponseEntity.ok().body(rentalService.getAllEquipment());
    }

    @PostMapping("/create")
    public ResponseEntity<Rental> createEquipmentRental(@RequestBody Rental rental){
        log.info("Call method createEquipmentRental");
        return ResponseEntity.ok().body(rentalService.createEquipmentRental(rental));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Rental> updateRental(
            @PathVariable("id") Long id,
            @RequestBody Rental rental
    ){
        log.info("Call method updateRental with id"+ id);
        return ResponseEntity.ok().body(rentalService.updateRental(id,rental));
    }
    @DeleteMapping("/{id}/cancel")
    public void rejectRental(
            @PathVariable("id") Long id
    ){
        log.info("Call method deleteRental with id"+ id);
        rentalService.rejectRental(id,RentalStatus.REJECTED);
    }
    @PostMapping("/approve/{id}")
    public ResponseEntity<Rental> approveRental(@PathVariable Long id){
        log.info("Call method approveRental with id"+ id);
        return ResponseEntity.ok().body(rentalService.approveRental(id));
    }
}
