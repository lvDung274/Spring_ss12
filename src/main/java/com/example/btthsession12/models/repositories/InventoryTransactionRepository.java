package com.example.btthsession12.repositories;

import com.example.btthsession12.models.dto.response.TopExportResponse;
import com.example.btthsession12.models.entities.InventoryTransaction;
import com.example.btthsession12.models.entities.TransactionType;
import com.example.btthsession12.services.DailyExportProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    @Query("SELECT t.supply.id as supplyId, t.supply.name as supplyName, SUM(t.amount) as totalExported " +
            "FROM InventoryTransaction t WHERE t.type = :type AND t.createdAt BETWEEN :start AND :end " +
            "GROUP BY t.supply.id, t.supply.name")
    List<DailyExportProjection> getDailyExportStats(
            @Param("type") TransactionType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT new com.example.btthsession12.models.dto.response.TopExportResponse(t.supply.name, SUM(t.amount)) " +
            "FROM InventoryTransaction t WHERE t.type = :type " +
            "GROUP BY t.supply.id, t.supply.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<TopExportResponse> findTopExportSupply(@Param("type") TransactionType type);
}