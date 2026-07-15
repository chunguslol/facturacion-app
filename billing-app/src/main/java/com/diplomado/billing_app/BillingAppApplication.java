package com.diplomado.billing_app;

import com.diplomado.billing_app.model.Factura;
import com.diplomado.billing_app.model.Usuario;
import com.diplomado.billing_app.repository.FacturaRepository;
import com.diplomado.billing_app.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.DigestUtils;

@SpringBootApplication
public class BillingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingAppApplication.class, args);
	}

	// Sembrador de datos: Esto crea los usuarios y facturas en PostgreSQL automáticamente
	@Bean
	public CommandLineRunner dataLoader(UsuarioRepository userRepo, FacturaRepository facturaRepo) {
		return args -> {
			// Solo los crea si la tabla está vacía
			if(userRepo.count() == 0) {
				// Usuario 1: admin / 123456 (Contraseña en MD5 vulnerable)
				userRepo.save(new Usuario("admin", DigestUtils.md5DigestAsHex("123456".getBytes()), "ADMIN"));
				// Usuario 2: competidor / hacker (Contraseña en MD5 vulnerable)
				userRepo.save(new Usuario("competidor", DigestUtils.md5DigestAsHex("hacker".getBytes()), "USER"));

				// Facturas del admin (ID 1)
				facturaRepo.save(new Factura(1L, "Empresa Alpha", "Pago de servidores", 5000.0));
				facturaRepo.save(new Factura(1L, "Empresa Beta", "Consultoría Seguridad", 8500.0));

				// Factura secreta del competidor (ID 2) - BOLA/IDOR Target
				facturaRepo.save(new Factura(2L, "Proyecto Secreto X", "Compra de vulnerabilidades", 99999.0));
			}
		};
	}
}