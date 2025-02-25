package ma.abid.customer_service.mapper;

import ma.abid.customer_service.dto.CustomerDTO;
import ma.abid.customer_service.entities.Customer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {
    CustomerMapper underTest = new CustomerMapper();

    @Test
    public void shouldMapCustomerToCustomerDTO(){
//        Arrange
        Customer givenCustomer = Customer.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").build();
        CustomerDTO expected = CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").build();

//        Acct
        CustomerDTO result =  underTest.fromCustomer(givenCustomer);

//        Assert
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public  void shouldMapCustomerDTOToCustomer(){
//        Arrange
        CustomerDTO givenCustomerDTO = CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").build();
        Customer expected = Customer.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").build();

//        Act
        Customer result =  underTest.fromCustomerDTO(givenCustomerDTO);

//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldMapCustomerListToCustomerDTOList(){
//        Arrange
        List<Customer> customerList = List.of(
                Customer.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").build(),
                Customer.builder().id(1L).firstName("Kamal").lastName("ABID").email("kamal@abid.cpm").build()
        );

        List<CustomerDTO> expected = List.of(
                CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").build(),
                CustomerDTO.builder().id(1L).firstName("Kamal").lastName("ABID").email("kamal@abid.cpm").build()
        );

//        Act
        List<CustomerDTO> result = underTest.customerDTOList(customerList);

//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldNotMapNullCustomerToCustomerDTO(){
//        Arrange
        Customer nullCustomer = null;
//        CustomerDTO expected = CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.cpm").build();

//        Act
//        CustomerDTO result = underTest.fromCustomer(nullCustomer);

//        Assert

        AssertionsForClassTypes.assertThatThrownBy(()->underTest.fromCustomer(nullCustomer)).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void shouldNotMapNullCustomerDTOToCustomer(){
        CustomerDTO customerDTO = null;
        AssertionsForClassTypes.assertThatThrownBy(()->underTest.fromCustomerDTO(customerDTO)).isInstanceOf(IllegalArgumentException.class);
    }

}