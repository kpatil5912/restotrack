package com.restotrack.repository;

import com.restotrack.entity.MovementType;
import com.restotrack.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByIngredientIdOrderByCreatedAtDesc(Long ingredientId);

    /** Wastage movements within a time window, most recent first. */
    @Query("""
            select m from StockMovement m
            where m.type = :type and m.createdAt >= :from and m.createdAt < :to
            order by m.createdAt desc
            """)
    List<StockMovement> findByTypeInRange(@Param("type") MovementType type,
                                          @Param("from") Instant from,
                                          @Param("to") Instant to);
}
