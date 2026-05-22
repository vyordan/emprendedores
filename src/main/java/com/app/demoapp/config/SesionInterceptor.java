package com.app.demoapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SesionInterceptor implements HandlerInterceptor {

    // Rutas que no requieren autenticación
    private static final String[] RUTAS_PUBLICAS = {
        "/", "/auth/login", "/auth/logout", "/auth/registro",
        "/trabajos", "/css/", "/js/", "/img/", "/imagenes/",
        "/error", "/favicon.ico"
    };

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        // Permitir rutas públicas
        for (String ruta : RUTAS_PUBLICAS) {
            if (uri.equals(ruta) || uri.startsWith(ruta)) {
                return true;
            }
        }

        // Permitir detalle de trabajo público
        if (uri.matches("/trabajos/\\d+")) {
            return true;
        }

        // Verificar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        return true;
    }
}