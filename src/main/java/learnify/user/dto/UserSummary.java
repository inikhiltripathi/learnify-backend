package learnify.user.dto;

import learnify.user.core.Role;
import learnify.user.core.UserEntity;
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
public class UserSummary {
                 
    private String name;
    private String email;
    private Role role;
    private String accessToken;  
    
    public static UserSummary map(UserEntity user, String token) {
        return UserSummary.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(token)
                .build();

    }
    
}
