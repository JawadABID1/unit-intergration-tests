package ma.abid.customer_service.service;

import ma.abid.customer_service.dto.CustomerDTO;
import ma.abid.customer_service.entities.Customer;
import ma.abid.customer_service.exception.CustomerNotFoundException;
import ma.abid.customer_service.exception.EmailAlreadyExistException;
import ma.abid.customer_service.mapper.CustomerMapper;
import ma.abid.customer_service.repository.CustomerRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    CustomerMapper customerMapper;
    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl underTest;

    @Test
    public void shouldSaveCustomer(){
//        Arrange
        CustomerDTO customerDTO = CustomerDTO.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        Customer customer = Customer.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        Customer savedCustomer = Customer.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        CustomerDTO expected = CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        Mockito.when(customerRepository.findByEmail(customerDTO.getEmail())).thenReturn(Optional.empty());
        Mockito.when(customerMapper.fromCustomerDTO(customerDTO)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(savedCustomer);
        Mockito.when(customerMapper.fromCustomer(savedCustomer)).thenReturn(expected);

//        Act
        CustomerDTO result = underTest.saveNewCustomer(customerDTO);

//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldNotSaveCustomerWhenItsEmailExist(){
//        Arrange
        CustomerDTO customerDTO = CustomerDTO.builder().firstName("Jawad").lastName("ABID").email("xxx@abid.com").build();
        Customer customer = Customer.builder().firstName("Jawad").lastName("ABID").email("xxx@abid.com").build();
        Mockito.when(customerRepository.findByEmail(customerDTO.getEmail())).thenReturn(Optional.of(customer));

//        Assert
        AssertionsForClassTypes.assertThatThrownBy(()->underTest.saveNewCustomer(customerDTO)).isInstanceOf(EmailAlreadyExistException.class)
                .hasMessage("Email already exists: "+customerDTO.getEmail());
    }

    @Test
    public void shouldGetAllCustomers(){
//        Arrange
        List<CustomerDTO> expected = List.of(
                CustomerDTO.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build(),
                CustomerDTO.builder().firstName("Kamal").lastName("ABID").email("kamal@abid.com").build()
        );
        List<Customer> customersList = List.of(
                Customer.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build(),
                Customer.builder().firstName("Kamal").lastName("ABID").email("kamal@abid.com").build()
        );
        Mockito.when(customerRepository.findAll()).thenReturn(customersList);
        Mockito.when(customerMapper.customerDTOList(customersList)).thenReturn(expected);

//        Act
        List<CustomerDTO> result = underTest.getAllCustomers();

//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(customersList).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldGetCustomerById(){
//        Arrange
        Long id = 1L;
        Customer customer = Customer.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        CustomerDTO expected = CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerMapper.fromCustomer(customer)).thenReturn(expected);

//        Act
        CustomerDTO result = underTest.getCustomerById(id);

//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(result);

    }

    @Test
    public void shouldNotGetCustomerById(){
//        Arrange
        Long id = 8L;
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.empty());

        AssertionsForClassTypes.assertThatThrownBy(()->underTest.getCustomerById(id)).isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("This Customer not found");
    }


    @Test
    public void shouldSearchCustomerByKeyWord(){
//        Arrange
        String keyword = "jaw";
        List<Customer> customerList = List.of(
                Customer.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build()
        );
        List<CustomerDTO> expected = List.of(
                CustomerDTO.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build()
        );
        Mockito.when(customerRepository.findByFirstNameContainsIgnoreCase(keyword)).thenReturn(customerList);
        Mockito.when(customerMapper.customerDTOList(customerList)).thenReturn(expected);

//        Act
        List<CustomerDTO> result = underTest.searchCustomers(keyword);

//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldUpdateCustomer(){
//        Arrange
        Long id = 1L;
        CustomerDTO customerDTO = CustomerDTO.builder().firstName("Jawadd").lastName("ABID").email("jawad@abid.com").build();
        Customer customer = Customer.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        Customer updatedCustomer = Customer.builder().id(1L).firstName("Jawadd").lastName("ABID").email("jawad@abid.com").build();
        CustomerDTO expected = CustomerDTO.builder().id(1L).firstName("Jawadd").lastName("ABID").email("jawad@abid.com").build();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerMapper.fromCustomerDTO(customerDTO)).thenReturn(updatedCustomer);
        Mockito.when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);
        Mockito.when(customerMapper.fromCustomer(updatedCustomer)).thenReturn(expected);

//        Act
        CustomerDTO result = underTest.upDateCustomer(id, customerDTO);

//        Assert
        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(expected).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    public void shouldNotUpdateCustomerNotExist(){
//        Arrange
        CustomerDTO customerDTO = CustomerDTO.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        Long id = 9L;
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.empty());

//        Assertion
        AssertionsForClassTypes.assertThatThrownBy(()->underTest.upDateCustomer(id, customerDTO)).isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("This Customer not found");
    }

    @Test
    public void shouldDeleteCustomer(){
//        Arrange
        Long id = 1L;
        Customer customer = Customer.builder().firstName("Jawad").lastName("ABID").email("jawad@abid.com").build();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

//        Act
        underTest.deleteCustomer(id);
        Mockito.verify(customerRepository).deleteById(id);

    }

    @Test
    public void shouldNotDeleteCustomerNotExist(){
        Long id = 9L;
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.empty());

        AssertionsForClassTypes.assertThatThrownBy(()->underTest.deleteCustomer(id)).isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("This Customer not found");
    }

}