package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepo extends JpaRepository<Book,Long>{
    Book findByName(String name);
}
