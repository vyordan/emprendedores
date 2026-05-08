package com.app.demoapp.config;

import com.app.demoapp.model.Billetera;
import com.app.demoapp.model.Categoria;
import com.app.demoapp.model.Usuario;
import com.app.demoapp.model.enums.EstadoUsuario;
import com.app.demoapp.model.enums.Rol;
import com.app.demoapp.repository.BilleteraRepository;
import com.app.demoapp.repository.CategoriaRepository;
import com.app.demoapp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BilleteraRepository billeteraRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            inicializarCategorias();
            inicializarAdmin();
        };
    }

    private void inicializarCategorias() {
        if (categoriaRepository.count() > 0) return;

        List<Categoria> categorias = List.of(
            categoria("Fontanería",       "fa-faucet"),
            categoria("Electricidad",     "fa-bolt"),
            categoria("Carpintería",      "fa-hammer"),
            categoria("Pintura",          "fa-paint-roller"),
            categoria("Jardinería",       "fa-leaf"),
            categoria("Limpieza",         "fa-broom"),
            categoria("Transporte",       "fa-truck"),
            categoria("Tecnología",       "fa-laptop"),
            categoria("Diseño",           "fa-pen-nib"),
            categoria("Cocina",           "fa-utensils"),
            categoria("Cuidado personal", "fa-heart"),
            categoria("Construcción",     "fa-hard-hat"),
            categoria("Educación",        "fa-book"),
            categoria("Otros",            "fa-ellipsis-h")
        );

        categoriaRepository.saveAll(categorias);
        System.out.println("✔ Categorías inicializadas.");
    }

    private void inicializarAdmin() {
        if (usuarioRepository.existsByEmail("admin@chamba.gt")) return;

        Usuario admin = new Usuario();
        admin.setEmail("admin@chamba.gt");
        admin.setPassword(passwordEncoder.encode("admin1234"));
        admin.setRol(Rol.ADMIN);
        admin.setEstado(EstadoUsuario.ACTIVO);
        admin = usuarioRepository.save(admin);

        Billetera billetera = new Billetera();
        billetera.setUsuario(admin);
        billetera.setSaldo(BigDecimal.ZERO);
        billeteraRepository.save(billetera);

        System.out.println("✔ Admin creado — email: admin@chamba.gt  password: admin1234");
    }

    private Categoria categoria(String nombre, String icono) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setIcono(icono);
        return c;
    }
}