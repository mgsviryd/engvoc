package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepo extends CrudRepository<VerificationToken, String> {
}
