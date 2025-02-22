package ma.abid.customer_service.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder @ToString
public class CustomerDTO {
    private Long id;
    @NotEmpty
    @Size(min = 2)
    private String firstName;
    @NotEmpty
    @Size(min = 2)
    private String lastName;
    @NotEmpty
    @Size(min = 8)
    private String email;
}
