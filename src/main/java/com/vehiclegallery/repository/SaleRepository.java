package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByCustomerId(Long customerId);

    List<Sale> findByListingDealerId(Long dealerId);

    List<Sale> findByStatus(String status);

    // =====================================================
    // AGGREGATE FONKSİYONLARI (Parametreli)
    // =====================================================

    // SUM: Belirli durumdaki satışların toplam tutarı
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Sale s WHERE s.status = :status")
    Double sumAmountByStatus(@Param("status") String status);

    // AVG: Belirli durumdaki satışların ortalama tutarı
    @Query("SELECT COALESCE(AVG(s.amount), 0) FROM Sale s WHERE s.status = :status")
    Double avgAmountByStatus(@Param("status") String status);

    // COUNT: Belirli durumdaki satış sayısı
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.status = :status")
    Long countByStatus(@Param("status") String status);

    // MIN: Belirli durumdaki en düşük satış tutarı
    @Query("SELECT COALESCE(MIN(s.amount), 0) FROM Sale s WHERE s.status = :status")
    Double minAmountByStatus(@Param("status") String status);

    // MAX: Belirli durumdaki en yüksek satış tutarı
    @Query("SELECT COALESCE(MAX(s.amount), 0) FROM Sale s WHERE s.status = :status")
    Double maxAmountByStatus(@Param("status") String status);

    // SUM: Belirli tarih aralığındaki satışların toplamı
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Sale s WHERE s.date BETWEEN :startDate AND :endDate")
    Double sumAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // COUNT: Belirli müşterinin satış sayısı
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);

    // AVG: Belirli müşterinin ortalama satış tutarı
    @Query("SELECT COALESCE(AVG(s.amount), 0) FROM Sale s WHERE s.customer.id = :customerId")
    Double avgAmountByCustomerId(@Param("customerId") Long customerId);
}
