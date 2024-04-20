package com.example.PKI;

import com.example.PKI.domain.Account;
import com.example.PKI.domain.CertificateRequest;
import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.KeyUsages;
import com.example.PKI.domain.enums.RequestStatus;
import com.example.PKI.domain.enums.Status;
import com.example.PKI.repository.KeyRepository;
import com.example.PKI.repository.KeyStoreRepository;
import com.example.PKI.service.CertificateGeneratorService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EntityScan("com.example.PKI.domain")
public class PkiApplication {

	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(PkiApplication.class, args);

	}

}
