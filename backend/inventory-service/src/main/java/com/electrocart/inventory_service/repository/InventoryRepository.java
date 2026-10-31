package com.electrocart.inventory_service.repository;

import com.electrocart.inventory_service.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    Optional<Inventory> findByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    //PESSIMISTIC_WRITE asks the database to lock the matching inventory row for writing.
    //If another transaction tries to lock the same row, it must wait until the first transaction completes.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.productId = :productId
            """)
    Optional<Inventory> findByProductIdForUpdate(
            @Param("productId") UUID productId
    );

}
