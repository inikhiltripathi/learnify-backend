package learnify.user.core;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long>{
    
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByRole(Role role);

}
