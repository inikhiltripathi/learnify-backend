package learnify.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import learnify.user.core.UserEntity;
import learnify.user.core.UserRepo;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService{

    
    private final UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        UserEntity userEntity = repo.findByEmail(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found")
        );
        return userEntity;
    }

}
