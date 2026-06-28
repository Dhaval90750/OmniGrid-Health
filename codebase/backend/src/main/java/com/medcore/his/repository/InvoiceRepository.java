package com.medcore.his.repository;

import com.medcore.his.domain.billing.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByStatusOrderByCreatedAtDesc(String status);

    @Query("SELECT SUM(i.netAmount) FROM Invoice i WHERE i.createdAt >= :startDate AND i.createdAt <= :endDate")
    BigDecimal sumNetAmountsForDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT v.department.name, SUM(i.netAmount) FROM Invoice i JOIN i.visit v WHERE i.createdAt >= :startDate AND i.createdAt <= :endDate GROUP BY v.department.name")
    List<Object[]> sumOpdRevenueByDepartment(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT w.department.name, SUM(i.netAmount) FROM Invoice i JOIN i.admission a JOIN a.ward w WHERE i.createdAt >= :startDate AND i.createdAt <= :endDate GROUP BY w.department.name")
    List<Object[]> sumIpdRevenueByDepartment(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
