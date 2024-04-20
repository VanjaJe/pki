package com.example.PKI;

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
//		CertificateRequest certificateRequest = new CertificateRequest();
//
//		Account account=new Account(1L,"name","pass", Status.ACTIVE,null,false);
//
//		User yourObject = new User();
//		yourObject.setId(1L);
//		yourObject.setFirstName("John");
//		yourObject.setLastName("Doe");
//		yourObject.setAddress(null);
//		yourObject.setPhoneNumber("123-456-7890");
//		yourObject.setAccount(account);
//		yourObject.setLastPasswordResetDate(null);
//		yourObject.setActivationLink("example_activation_link");
//		yourObject.setActivationLinkDate(null);
//		yourObject.setReportingReason("example_reporting_reason");
//		yourObject.setDeleted(false);
//		yourObject.setAccount(account);
//
//
//
//		// Set properties of the CertificateRequest object
//		certificateRequest.setSubject(yourObject); // Assuming User class exists and provides a default constructor
//		certificateRequest.setIssuerAlias("root"); // Assuming Certificate class exists and provides a default constructor
//		certificateRequest.setDate(new Date());
//		certificateRequest.setRequestStatus(RequestStatus.ACTIVE);
//		certificateRequest.setCertificateType(CertificateType.INTERMEDIATE); // Assuming CertificateType enum exists
//		List<KeyUsages> keyUsages = new ArrayList<>();
//		keyUsages.add(KeyUsages.DIGITAL_SIGNATURE);
//		certificateRequest.setKeyUsages(keyUsages);
//
//		CertificateGeneratorService service=new CertificateGeneratorService();
//		service.generateCertificate(certificateRequest);
//
//		KeyStoreRepository keyStoreRepository=new KeyStoreRepository();
//		Collection<Certificate>certificates=keyStoreRepository.readCertificates();
//		for (Certificate certificate:certificates){
//			System.out.println(certificate);
//		}







	}

}
