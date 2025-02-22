package ma.abid.customer_service.repository;

import ma.abid.customer_service.entities.Customer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    public void setUp(){
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
    }

    @Test
    public void shouldFindCustomerByEmail(){
//        Arrange
        String givenEmail = "jawad@abid.com";

//        Act
        Optional<Customer> reuslt = customerRepository.findByEmail(givenEmail);

//        Assert
        AssertionsForClassTypes.assertThat(reuslt).isPresent();
    }

    @Test
    public void shouldNotFindCustomerByEmail(){
//        Arrange
        String giveEmail = "jawad@jawad.com";

//        Act
        Optional<Customer> result = customerRepository.findByEmail(giveEmail);

//        Assert
        AssertionsForClassTypes.assertThat(result).isEmpty();

    }

    @Test
    public void shouldFinCustomerByFirstName(){
//        Arrange
        String keyword = "l";
        List<Customer> expectedList = List.of(
                Customer.builder().firstName("Kamal").lastName("ABID").email("kamal@abid.com").build(),
        Customer.builder().firstName("Bilal").lastName("ABID").email("bilal@abid.com").build()
        );

//        Act
        List<Customer> result = customerRepository.findByFirstNameContainsIgnoreCase(keyword);


//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(result.size()).isEqualTo(expectedList.size());
        AssertionsForClassTypes.assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedList);
    }

}