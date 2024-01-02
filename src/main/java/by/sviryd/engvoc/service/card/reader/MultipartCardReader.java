package by.sviryd.engvoc.service.card.reader;

import by.sviryd.engvoc.domain.Card;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MultipartCardReader {
    List<Card> extract(MultipartFile file) throws IOException;
}
