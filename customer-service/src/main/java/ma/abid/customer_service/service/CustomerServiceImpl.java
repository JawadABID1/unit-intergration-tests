package ma.abid.customer_service.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ma.abid.customer_service.dto.CustomerDTO;
import ma.abid.customer_service.entities.Customer;
import ma.abid.customer_service.exception.CustomerNotFoundException;
import ma.abid.customer_service.exception.EmailAlreadyExistException;
import ma.abid.customer_service.mapper.CustomerMapper;
import ma.abid.customer_service.repository.CustomerRepository;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) throws EmailAlreadyExistException {
//        log.info(String.format("saving new Customer=> %s", customerDTO.toString()));
        Optional<Customer> byEmail = customerRepository.findByEmail(customerDTO.getEmail());
        if(byEmail.isPresent()){
//            log.error(String.format("This email %s already exist", customerDTO.getEmail()));
            throw new EmailAlreadyExistException("Email already exists: " + customerDTO.getEmail());
        }
        Customer customerToSave = customerMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customerToSave);
        return customerMapper.fromCustomer(savedCustomer);

    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> allCustomers = customerRepository.findAll();
        return customerMapper.customerDTOList(allCustomers);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) throws CustomerNotFoundException {
        Optional<Customer> searchedCustomer = customerRepository.findById(id);
        if(searchedCustomer.isEmpty()) throw new  CustomerNotFoundException("This Customer not found");
        return customerMapper.fromCustomer(searchedCustomer.get());
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> searchedCustomers = customerRepository.findByFirstNameContainsIgnoreCase(keyword);
        return customerMapper.customerDTOList(searchedCustomers);
    }

    @Override
    public CustomerDTO upDateCustomer(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException {
        Optional<Customer> customerToUpdate = customerRepository.findById(id);
        if(customerToUpdate.isEmpty()) throw new CustomerNotFoundException("This Customer not found");
        customerDTO.setId(id);
        Customer customerToSave = customerMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customerToSave);
        return customerMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException {
        Optional<Customer> customerToDelete = customerRepository.findById(id);
        if(customerToDelete.isEmpty()) throw new CustomerNotFoundException("This Customer not found");
        customerRepository.deleteById(id);
    }
}
