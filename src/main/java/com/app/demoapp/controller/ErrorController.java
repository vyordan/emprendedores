package com.app.demoapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController implements
        org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping
    public String manejarError(HttpServletRequest request) {
        Integer codigo = (Integer) request.getAttribute(
                "jakarta.servlet.error.status_code");
        if (codigo != null) {
            if (codigo == HttpStatus.NOT_FOUND.value())      return "error/404";
            if (codigo == HttpStatus.FORBIDDEN.value())      return "error/403";
        }
        return "error/404";
    }
}