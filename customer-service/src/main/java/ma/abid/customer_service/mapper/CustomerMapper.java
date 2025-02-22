package ma.abid.customer_service.mapper;

import ma.abid.customer_service.dto.CustomerDTO;
import ma.abid.customer_service.entities.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerMapper {
    private ModelMapper modelMapper = new ModelMapper();

    public CustomerDTO fromCustomer(Customer customer){
        return modelMapper.map(customer, CustomerDTO.class);
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO){
        return modelMapper.map(customerDTO, Customer.class);
    }

    public List<CustomerDTO> customerDTOList(List<Customer> customerList){
        return customerList.stream().map(c->modelMapper.map(c, CustomerDTO.class)).collect(Collectors.toList());
    }

}
