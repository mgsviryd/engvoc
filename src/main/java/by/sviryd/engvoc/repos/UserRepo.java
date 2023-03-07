package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findBySub(String sub);
    Iterable<User> findAllByToken(Iterable<String> tokens);
}
