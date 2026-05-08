package com.app.demoapp.util;

import java.util.Base64;

public class ImagenUtil {

    private ImagenUtil() {}

    // Genera el src completo para usar en <img th:src="...">
    public static String toBase64Src(byte[] datos, String tipo) {
        if (datos == null || datos.length == 0) return null;
        String base64 = Base64.getEncoder().encodeToString(datos);
        return "data:" + tipo + ";base64," + base64;
    }
}