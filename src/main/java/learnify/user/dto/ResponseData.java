package learnify.user.dto;

import learnify.user.core.Role;
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
public class ResponseData {
                 
    private String name;
    private String email;
    private Role role;
    private String accessToken;              
    
}
