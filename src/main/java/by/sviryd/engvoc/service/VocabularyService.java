package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.repos.VocabularyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class VocabularyService {
    @Autowired
    private VocabularyRepo vocabularyRepo;

    public Vocabulary save(Vocabulary pair){
        return vocabularyRepo.save(pair);
    }
    public Optional<Vocabulary> findById(UUID id){
        return vocabularyRepo.findById(id);
    }
}
