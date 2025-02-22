package ma.abid.customer_service.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.source.tree.ModuleTree;
import ma.abid.customer_service.dto.CustomerDTO;
import ma.abid.customer_service.exception.CustomerNotFoundException;
import ma.abid.customer_service.exception.EmailAlreadyExistException;
import ma.abid.customer_service.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.hamcrest.Matchers;

import java.util.List;

@WebMvcTest(CustomerRestController.class)
@ActiveProfiles("test")
class CustomerRestControllerTest {
    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private List<CustomerDTO> customerDTOList;

    @BeforeEach
    void setUp() {
        this.customerDTOList = List.of(
                CustomerDTO.builder().id(1L).firstName("Jawad").lastName("ABID").email("jawad@abid.com").build(),
                CustomerDTO.builder().id(2L).firstName("Kamal").lastName("ABID").email("kamal@abid.com").build(),
                CustomerDTO.builder().id(3L).firstName("Bilal").lastName("ABID").email("bilal@abid.com").build()
        );
    }

    @Test
    void shouldGetAllCustomers() throws Exception {
        Mockito.when(customerService.getAllCustomers()).thenReturn(customerDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTOList)));
    }

    @Test
    public void shouldGetCustomerById() throws Exception{
        Long id = 1L;
         Mockito.when(customerService.getCustomerById(id)).thenReturn(customerDTOList.get(0));

         mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/{id}", id))
                 .andExpect(MockMvcResultMatchers.status().isOk())
                 .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTOList.get(0))));
    }

    @Test
    public void shouldNotGteCustomerByInvalidId() throws Exception {
        Long id = 9L;
        Mockito.when(customerService.getCustomerById(id)).thenThrow(CustomerNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));

    }

    @Test
    public void shouldSearchCustomers() throws Exception {
        String keyword = "a";
        Mockito.when(customerService.searchCustomers(keyword)).thenReturn(customerDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/search?keyword="+keyword))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTOList)));
    }

    @Test
    void shouldSaveCustomer() throws Exception {
        CustomerDTO customerDTO = customerDTOList.get(0);
        String expected = objectMapper.writeValueAsString(customerDTO);

        Mockito.when(customerService.saveNewCustomer(Mockito.any())).thenReturn(customerDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers") // Ensure this matches your actual controller endpoint
                        .content(objectMapper.writeValueAsString(customerDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(expected));
    }

    @Test
    void shouldNotSaveCustomerWithEmailExist() throws Exception {
        CustomerDTO customerDTO = customerDTOList.get(0);
        Mockito.when(customerService.saveNewCustomer(Mockito.any()))
                .thenThrow(new EmailAlreadyExistException("Email already exists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isConflict()) // Expecting 409 Conflict
                .andExpect(MockMvcResultMatchers.content().string("Email already exists"));
    }


    @Test
    public void shouldUpdateCustomerGivenById() throws Exception {
        Long id = 1L;
        CustomerDTO customerDTO = customerDTOList.get(0);

        Mockito.when(customerService.upDateCustomer(Mockito.eq(id), Mockito.any())).thenReturn(customerDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/customers/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(customerDTO)));

    }
    @Test
    public void shouldNotUpdateCustomerGivenByInvalidId() throws Exception {
        Long id = 9L;
        CustomerDTO customerDTO = CustomerDTO.builder()
                .firstName("Jawad")
                .lastName("ABID")  // Corrected lastName
                .email("jawad@abid.com")
                .build();

        Mockito.when(customerService.upDateCustomer(Mockito.eq(id), Mockito.any()))
                .thenThrow(new CustomerNotFoundException("This Customer not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/customers/{id}", id)
                        .content(objectMapper.writeValueAsString(customerDTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("This Customer not found"));
    }

    @Test
    public void shouldDeleteGivenCustomerById() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(customerService).deleteCustomer(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }


    @Test
    public void shouldNotDeleteGivenCustomerByInvalidId() throws Exception {
        Long invalidId = 9L;
        Mockito.doThrow(new CustomerNotFoundException("This customer not found")).when(customerService).deleteCustomer(invalidId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/{id}", invalidId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("This customer not found"));
    }

}
