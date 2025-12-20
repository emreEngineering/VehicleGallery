package com.vehiclegallery.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // Galerici (DEALER) yetkisi gerektiren URL pattern'leri
    private static final String[] DEALER_ONLY_PATTERNS = {
            "/vehicles/new", "/vehicles/edit", "/vehicles/delete",
            "/listings/new", "/listings/delete",
            "/customers", "/services",
            "/sales/new", "/sales/.*edit", "/sales/.*delete",
            "/rentals/new", "/rentals/.*edit", "/rentals/.*delete"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String uri = request.getRequestURI();

        // Login, register ve static kaynakları atla
        if (isPublicResource(uri)) {
            return true;
        }

        HttpSession session = request.getSession(false);

        // Session kontrolü
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/login");
            return false;
        }

        String userType = (String) session.getAttribute("userType");

        // DEALER yetkisi gerektiren endpoint'ler için kontrol
        if (isDealerOnlyEndpoint(uri) && !"DEALER".equals(userType)) {
            response.sendRedirect("/?error=unauthorized");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object user = session.getAttribute("user");
                String userType = (String) session.getAttribute("userType");

                modelAndView.addObject("currentUser", user);
                modelAndView.addObject("userType", userType);
                modelAndView.addObject("isDealer", "DEALER".equals(userType));
                modelAndView.addObject("isCustomer", "CUSTOMER".equals(userType));
            }
        }
    }

    private boolean isPublicResource(String uri) {
        return uri.startsWith("/login") ||
                uri.startsWith("/register") ||
                uri.startsWith("/logout") ||
                uri.startsWith("/css") ||
                uri.startsWith("/js") ||
                uri.startsWith("/images") ||
                uri.startsWith("/webjars") ||
                uri.startsWith("/error");
    }

    private boolean isDealerOnlyEndpoint(String uri) {
        for (String pattern : DEALER_ONLY_PATTERNS) {
            if (uri.matches(pattern) || uri.startsWith(pattern.replace(".*", ""))) {
                return true;
            }
        }
        return false;
    }
}
