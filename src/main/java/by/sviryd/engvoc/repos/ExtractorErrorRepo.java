package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.ExtractorError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtractorErrorRepo extends JpaRepository<ExtractorError, Long> {
}
