package ma.abid.customer_service;

import ma.abid.customer_service.entities.Customer;
import ma.abid.customer_service.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	@Profile("!test")
	CommandLineRunner commandLineRunner(CustomerRepository customerRepository){
		return args -> {
			customerRepository.save(Customer.builder()
							.firstName("Jawad")
							.lastName("ABID")
							.email("jawad@abid.com")
					.build());
			customerRepository.save(Customer.builder()
					.firstName("Kamal")
					.lastName("ABID")
					.email("kamal@abid.com")
					.build());
			customerRepository.save(Customer.builder()
					.firstName("Bilal")
					.lastName("ABID")
					.email("bilal@abid.com")
					.build());
		};
	}
}
