package ma.abid.customer_service.service;

import ma.abid.customer_service.dto.CustomerDTO;
import ma.abid.customer_service.exception.CustomerNotFoundException;
import ma.abid.customer_service.exception.EmailAlreadyExistException;
import ma.abid.customer_service.mapper.CustomerMapper;

import java.util.List;

public interface CustomerService {
    CustomerDTO saveNewCustomer(CustomerDTO customerDTO) throws EmailAlreadyExistException;
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(Long id) throws CustomerNotFoundException;
    List<CustomerDTO> searchCustomers(String keyword);
    CustomerDTO upDateCustomer(Long id, CustomerDTO customerDTO) throws CustomerNotFoundException;
    void deleteCustomer(Long id) throws CustomerNotFoundException;
}
