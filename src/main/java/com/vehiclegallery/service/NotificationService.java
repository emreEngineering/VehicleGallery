package com.vehiclegallery.service;

import com.vehiclegallery.entity.Customer;
import com.vehiclegallery.entity.Notification;
import com.vehiclegallery.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> findByCustomerId(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Notification> findUnreadByCustomerId(Long customerId) {
        return notificationRepository.findByCustomerIdAndIsReadFalseOrderByCreatedAtDesc(customerId);
    }

    public long countUnread(Long customerId) {
        return notificationRepository.countByCustomerIdAndIsReadFalse(customerId);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(Long customerId) {
        List<Notification> unread = findUnreadByCustomerId(customerId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    /**
     * Satış onay bildirimi gönder
     */
    public void sendSaleApprovedNotification(Customer customer, Long saleId, String vehicleInfo) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setTitle("Satın Alma Talebiniz Onaylandı! ✓");
        notification.setMessage("'" + vehicleInfo + "' için satın alma talebiniz galerici tarafından onaylandı. " +
                "Tebrikler! Ödeme ve teslimat için sizinle iletişime geçilecektir.");
        notification.setType("SUCCESS");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRelatedSaleId(saleId);
        save(notification);
    }

    /**
     * Satış red bildirimi gönder
     */
    public void sendSaleRejectedNotification(Customer customer, Long saleId, String vehicleInfo) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setTitle("Satın Alma Talebiniz Reddedildi");
        notification.setMessage("'" + vehicleInfo + "' için satın alma talebiniz maalesef reddedildi. " +
                "Başka araçlarımızı inceleyebilirsiniz.");
        notification.setType("ERROR");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRelatedSaleId(saleId);
        save(notification);
    }

    /**
     * Kiralama onay bildirimi gönder
     */
    public void sendRentalApprovedNotification(Customer customer, Long rentalId, String vehicleInfo) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setTitle("Kiralama Talebiniz Onaylandı! ✓");
        notification.setMessage("'" + vehicleInfo + "' için kiralama talebiniz galerici tarafından onaylandı. " +
                "Aracınız belirtilen tarihte hazır olacaktır.");
        notification.setType("SUCCESS");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRelatedRentalId(rentalId);
        save(notification);
    }

    /**
     * Kiralama red bildirimi gönder
     */
    public void sendRentalRejectedNotification(Customer customer, Long rentalId, String vehicleInfo) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setTitle("Kiralama Talebiniz Reddedildi");
        notification.setMessage("'" + vehicleInfo + "' için kiralama talebiniz maalesef reddedildi. " +
                "Farklı tarihler veya araçlar için tekrar deneyebilirsiniz.");
        notification.setType("ERROR");
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRelatedRentalId(rentalId);
        save(notification);
    }
}
