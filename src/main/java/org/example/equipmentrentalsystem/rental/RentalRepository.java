package org.example.equipmentrentalsystem.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<RentalEntity,Long> {
    @Modifying
    @Query("""
update RentalEntity r 
set r.status=:status
where r.id=:id
""")
    void setStatus(@Param("id") Long id, @Param("status") RentalStatus rentalStatus);


    @Query("""
select r from RentalEntity r
where r.equipment=:equipment
and :startDate=r.endDate
and r.startDate=:endDate
""")
    List<Long> findConflict(
            @Param("equipment") String equipment,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
