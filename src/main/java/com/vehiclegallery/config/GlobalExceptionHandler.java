package com.vehiclegallery.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;

/**
 * Global Exception Handler
 * Tüm controller'lardaki hataları merkezi olarak yakalar ve kullanıcı dostu
 * mesajlar gösterir.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Tarih parse hatalarını yakala
     */
    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateTimeParseException(DateTimeParseException ex,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("error", "Geçersiz tarih formatı! Lütfen tarihleri YYYY-MM-DD formatında giriniz.");
        model.addAttribute("errorDetails", ex.getMessage());
        return "error/general";
    }

    /**
     * Veritabanı constraint ihlallerini yakala (duplicate key, foreign key vb.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex,
            HttpServletRequest request,
            Model model) {
        String message = "Veritabanı hatası oluştu.";
        String errorMessage = ex.getMessage();

        // Log the full error for debugging
        System.err.println("Database Error: " + errorMessage);
        if (ex.getCause() != null) {
            System.err.println("Cause: " + ex.getCause().getMessage());
        }

        if (errorMessage != null) {
            String lowerError = errorMessage.toLowerCase();
            if (lowerError.contains("email")) {
                message = "Bu e-posta adresi zaten kayıtlı!";
            } else if (lowerError.contains("national_id")) {
                message = "Bu TC Kimlik numarası zaten kayıtlı!";
            } else if (lowerError.contains("tax_number")) {
                message = "Bu vergi numarası zaten kayıtlı!";
            } else if (lowerError.contains("plate_number") || lowerError.contains("plate_num")) {
                message = "Bu plaka numarası zaten kayıtlı!";
            } else if (lowerError.contains("chassis_number")) {
                message = "Bu şasi numarası zaten kayıtlı!";
            } else if (lowerError.contains("foreign key") || lowerError.contains("foreign_key")) {
                message = "İşlem başarısız: Veritabanı kısıtlaması hatası (Bağlı kayıtlar veya geçersiz referans).";
            } else if (lowerError.contains("unique") || lowerError.contains("duplicate")) {
                message = "Bu kayıt zaten mevcut (Tekrarlanan Veri).";
            }
        }

        model.addAttribute("error", message);
        model.addAttribute("errorDetails", errorMessage); // Show details for now to help debug
        return "error/general";
    }

    /**
     * NullPointerException yakala
     */
    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException ex,
            HttpServletRequest request,
            Model model) {
        ex.printStackTrace(); // Log stack trace
        model.addAttribute("error", "Beklenmeyen bir hata oluştu. Lütfen tüm alanları doldurduğunuzdan emin olun.");
        model.addAttribute("errorDetails", "Eksik veri hatası: " + ex.getMessage());
        return "error/general";
    }

    /**
     * IllegalArgumentException yakala
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("error", "Geçersiz değer girildi: " + ex.getMessage());
        return "error/general";
    }

    /**
     * NumberFormatException yakala
     */
    @ExceptionHandler(NumberFormatException.class)
    public String handleNumberFormatException(NumberFormatException ex,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("error", "Geçersiz sayı formatı! Lütfen sayısal değerleri doğru giriniz.");
        return "error/general";
    }

    /**
     * Genel Exception yakala (catch-all)
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex,
            HttpServletRequest request,
            Model model) {
        ex.printStackTrace(); // Log full stack trace
        model.addAttribute("error", "Beklenmeyen bir hata oluştu.");
        model.addAttribute("errorDetails", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        return "error/general";
    }
}
