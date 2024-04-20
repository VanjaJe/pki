package com.example.PKI;

import com.example.PKI.repository.KeyStoreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan("com.example.PKI.domain")
public class PkiApplication {

	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(PkiApplication.class, args);
//
//		KeyStoreRepository repository=new KeyStoreRepository();
//		repository.createKeystore();
//		repository.writeRootCACertificate("root");

	}

}
