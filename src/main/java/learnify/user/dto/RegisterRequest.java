package learnify.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter 
@Setter
public class RegisterRequest {

    @NotBlank(message = "Name is Required")
    private String name;

    @Email(message = "Invalid Email format")
    @NotBlank(message = "Email is Required")
    private String email;

    @Size(min = 6, message = "Password must be 6 to 8 characters")
    private String password;

}
