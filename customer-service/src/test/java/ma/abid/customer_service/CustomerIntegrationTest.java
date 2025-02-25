package ma.abid.customer_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import ma.abid.customer_service.dto.CustomerDTO;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ExtendWith(SpringExtension.class)
public class CustomerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    private List<CustomerDTO> customerDTOList;

    @BeforeEach
    public void setup() {
        this.customerDTOList = new ArrayList<>();
        this.customerDTOList.add(CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.com").build());
        this.customerDTOList.add(CustomerDTO.builder().id(2L).firstName("Kamal").lastName("ABID").email("kamal@abid.com").build());
        this.customerDTOList.add(CustomerDTO.builder().id(3L).firstName("Bilal").lastName("ABID").email("bilal@abid.com").build());
    }

    /**
     * Test for retrieving all Customers
     */
    @Test
    void shouldFetchAllCustomers() {
        ResponseEntity<CustomerDTO[]> response = testRestTemplate.exchange(
                "/api/customers",
                HttpMethod.GET,
                null,
                CustomerDTO[].class);
        List<CustomerDTO> content = Arrays.asList(Objects.requireNonNull(response.getBody()));

        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(content.size()).isEqualTo(customerDTOList.size());
        AssertionsForClassTypes.assertThat(content).usingRecursiveComparison().isEqualTo(customerDTOList);
    }

    /**
     * Test to retrieving searched Customers by their firstName
     */
    @Test
    void shouldFetchSearchedCustomers() {
        String keyword = "al";
        ResponseEntity<CustomerDTO[]> response = testRestTemplate.exchange(
                "/api/customers/search?keyword=" + keyword,
                HttpMethod.GET,
                null,
                CustomerDTO[].class);
        List<CustomerDTO> customers = Arrays.asList(Objects.requireNonNull(response.getBody()));

        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(customers.size()).isEqualTo(2);

        List<CustomerDTO> expected = customerDTOList.stream().filter(c -> c.getFirstName().toUpperCase().contains(keyword.toUpperCase())).toList();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(customers);
    }

    /**
     * Test to retrieving Customer by its id
     */
    @Test
    void shouldFetchCustomerById(){
        Long id = 1L;
        CustomerDTO expected = customerDTOList.get(0);
        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange(
                "/api/customers/"+id,
                HttpMethod.GET,
                null,
                CustomerDTO.class);
        CustomerDTO content = Objects.requireNonNull(response.getBody());
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(content).isNotNull();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(content);
    }

    /**
     * Test to throwing exception if customer not found
     */
    @Test
    void shouldNotFetchCustomerByIdNotFound(){
        Long invalidId = 9L;
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/customers/"+invalidId,
                HttpMethod.GET,
                null,
                String.class);
        String content = response.getBody();
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        AssertionsForClassTypes.assertThat(content).isNotNull();
        AssertionsForClassTypes.assertThat(content).isEqualTo("This Customer not found");
    }
    /**
     * Test to updating given customer
     */
    @Test
    @Rollback
    void shouldUpdateGivenCustomer() {
        Long id = 1L;

        CustomerDTO updatedCustomer = CustomerDTO.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updated@example.com")
                .build();
        HttpEntity<CustomerDTO> requestEntity = new HttpEntity<>(updatedCustomer);

        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange(
                "/api/customers/" + id,
                HttpMethod.PUT,
                requestEntity,
                CustomerDTO.class
        );

        System.out.println("response: "+ response);

        // Assertions
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(response.getBody()).isNotNull();
        AssertionsForClassTypes.assertThat(response.getBody()).usingRecursiveComparison().ignoringFields("id").isEqualTo(updatedCustomer);
    }

    @Test
    void shouldNotUpdateGivenCustomer() {
        Long id = 9L;

        // Create an updated CustomerDTO object (This will not be used since the customer doesn't exist)
        CustomerDTO updatedCustomer = CustomerDTO.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updated@example.com")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CustomerDTO> requestEntity = new HttpEntity<>(updatedCustomer, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/customers/" + id,
                HttpMethod.PUT,
                requestEntity,
                String.class // Change the response body type to String to match the error message
        );

        // Assertions
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        AssertionsForClassTypes.assertThat(response.getBody()).isNotNull();
        AssertionsForClassTypes.assertThat(response.getBody()).isEqualTo("This Customer not found");
    }

    /**
     * Test to delete customer
     */
    @Test
    @Rollback
    void shouldDeleteCustomer() {
        Long id = 2L;
        ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/customers/" + id,
                HttpMethod.DELETE,
                null,
                Void.class);

        // Assert DELETE response
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


    /**
     * Test to not delete Customer dos not exist
     */
    @Test
    void shouldNotDeleteNonExistingCustomer() {
        Long invalidId = 99L;

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/customers/" + invalidId,
                HttpMethod.DELETE,
                null,
                String.class
        );

        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        AssertionsForClassTypes.assertThat(response.getBody()).isEqualTo("This Customer not found");
    }

    /**
     * Test to save valid customer
     */
    @Test
    @Rollback
    void shouldSaveValidCustomer(){
        CustomerDTO customerToSave = CustomerDTO.builder().firstName("Mohamed").lastName("ABID").email("mohamed@abid.com").build();

        ResponseEntity<CustomerDTO> response = testRestTemplate.exchange(
                "/api/customers",
                HttpMethod.POST,
                new HttpEntity<>(customerToSave),
                CustomerDTO.class);
        CustomerDTO content = response.getBody();

        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        AssertionsForClassTypes.assertThat(content).usingRecursiveComparison().ignoringFields("id").isEqualTo(customerToSave);
    }

    /**
     * Test to not save invalid customer
     */
    @Test
    void shouldNotSaveInvalidCustomer() throws JsonProcessingException {
        CustomerDTO invalidCustomer = CustomerDTO.builder()
                .firstName("")
                .lastName("")
                .email("")
                .build();

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/customers",
                HttpMethod.POST,
                new HttpEntity<>(invalidCustomer),
                String.class
        );

        // Debug: Print response body
        System.out.println("Response Body: " + response.getBody());

        // Assert response status is 400 BAD_REQUEST
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Deserialize response body into Map
        Map<String, String> errors = objectMapper.readValue(response.getBody(), HashMap.class);

        // Ensure that validation errors exist
        AssertionsForClassTypes.assertThat(errors.containsKey("firstName")).isTrue();
        AssertionsForClassTypes.assertThat(errors.containsKey("lastName")).isTrue();
        AssertionsForClassTypes.assertThat(errors.containsKey("email")).isTrue();

        // Ensure each field has an error message
        AssertionsForClassTypes.assertThat(errors.get("firstName")).isNotEmpty();
        AssertionsForClassTypes.assertThat(errors.get("lastName")).isNotEmpty();
        AssertionsForClassTypes.assertThat(errors.get("email")).isNotEmpty();
    }
    /**
     * Test for Not create a new customer with email exist
     */
    @Test
    void shouldNotSaveNewCustomerWithExsitedEmail(){
        CustomerDTO customerDTO = CustomerDTO.builder()
                .firstName("test")
                .lastName("test")
                .email("jawad@abid.com")
                .build();

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/customers",
                HttpMethod.POST,
                new HttpEntity<>(customerDTO),
                String.class
        );
        String content = response.getBody();
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        AssertionsForClassTypes.assertThat(content).contains("Email already exists: "+customerDTO.getEmail());
    }



}


