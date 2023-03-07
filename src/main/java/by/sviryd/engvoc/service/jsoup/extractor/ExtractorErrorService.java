package by.sviryd.engvoc.service.jsoup.extractor;

import by.sviryd.engvoc.domain.ExtractorError;
import by.sviryd.engvoc.repos.ExtractorErrorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtractorErrorService {
    @Autowired
    private ExtractorErrorRepo errorRepo;

    public ExtractorError save(ExtractorError extractorError) {
        return errorRepo.save(extractorError);
    }
}
