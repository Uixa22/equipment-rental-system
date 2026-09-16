package org.example.equipmentrentalsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<RentalEntity,Long> {
    @Modifying
    @Query("""
update RentalEntity r 
set r.status=:status
where r.id=:id
""")
    default void setStatus(@Param("id") Long id, @Param("status") RentalStatus rentalStatus) {
    }
}
