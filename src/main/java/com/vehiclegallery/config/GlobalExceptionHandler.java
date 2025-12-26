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
        if (errorMessage != null) {
            if (errorMessage.contains("email")) {
                message = "Bu e-posta adresi zaten kayıtlı!";
            } else if (errorMessage.contains("national_id")) {
                message = "Bu TC Kimlik numarası zaten kayıtlı!";
            } else if (errorMessage.contains("tax_number")) {
                message = "Bu vergi numarası zaten kayıtlı!";
            } else if (errorMessage.contains("plate_number")) {
                message = "Bu plaka numarası zaten kayıtlı!";
            } else if (errorMessage.contains("chassis_number")) {
                message = "Bu şasi numarası zaten kayıtlı!";
            } else if (errorMessage.contains("foreign key") || errorMessage.contains("FOREIGN KEY")) {
                message = "Bu kayıt başka kayıtlarla ilişkili olduğu için silinemez!";
            }
        }

        model.addAttribute("error", message);
        return "error/general";
    }

    /**
     * NullPointerException yakala
     */
    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException ex,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("error", "Beklenmeyen bir hata oluştu. Lütfen tüm alanları doldurduğunuzdan emin olun.");
        model.addAttribute("errorDetails", "Eksik veri hatası");
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
        model.addAttribute("error", "Beklenmeyen bir hata oluştu.");
        model.addAttribute("errorDetails", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        return "error/general";
    }
}
