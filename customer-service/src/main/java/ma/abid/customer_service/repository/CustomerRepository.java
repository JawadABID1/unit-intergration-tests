package ma.abid.customer_service.repository;

import ma.abid.customer_service.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    List<Customer> findByFirstNameContainsIgnoreCase(String keyword);

}
