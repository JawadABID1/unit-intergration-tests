package ma.abid.customer_service.web;

import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import lombok.Getter;
import ma.abid.customer_service.dto.CustomerDTO;
import ma.abid.customer_service.entities.Customer;
import ma.abid.customer_service.exception.EmailAlreadyExistException;
import ma.abid.customer_service.repository.CustomerRepository;
import ma.abid.customer_service.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerRestController {
    private CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomerById(@PathVariable Long id){
        return customerService.getCustomerById(id);
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> getSearchedCustomers(@RequestParam String keyword){
        return customerService.searchCustomers(keyword);
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO saveNewCustomer(@RequestBody @Valid CustomerDTO customerDTO){
        return customerService.saveNewCustomer(customerDTO);
    }

    @PutMapping("/customers/{id}")
    public CustomerDTO updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerDTO customerDTO){
        return customerService.upDateCustomer(id, customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id){
        customerService.deleteCustomer(id);
    }
}


