package learnify.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ResetPassword {

    @Email(message = "Invalid Email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be 6 to 8 characters")
    private String password;
    private String otp;


}
