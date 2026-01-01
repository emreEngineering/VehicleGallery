package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Müşteriye ait tüm bildirimler (en yeniden eskiye)
    List<Notification> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Okunmamış bildirimler
    List<Notification> findByCustomerIdAndIsReadFalseOrderByCreatedAtDesc(Long customerId);

    // Okunmamış bildirim sayısı
    long countByCustomerIdAndIsReadFalse(Long customerId);
}
