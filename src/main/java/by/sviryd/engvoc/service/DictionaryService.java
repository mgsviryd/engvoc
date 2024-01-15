package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.repos.DictionaryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DictionaryService {
    @Autowired
    private DictionaryRepo dictionaryRepo;

    public void delete(Dictionary dictionary) {
        dictionaryRepo.delete(dictionary);
    }

    public Dictionary save(Dictionary dictionary) {
        return dictionaryRepo.save(dictionary);
    }

    public long count() {
        return dictionaryRepo.count();
    }

    public Page<Dictionary> findAll(Pageable pageable) {
        return dictionaryRepo.findAll(pageable);
    }

    public Dictionary saveAndFlush(Dictionary dictionary) {
        return dictionaryRepo.saveAndFlush(dictionary);
    }

    public Optional<Dictionary> findByName(String name) {
        return dictionaryRepo.findByName(name);
    }

    public Optional<Dictionary> findByNameAndUnrepeated(String name, Boolean unrepeated) {
        return dictionaryRepo.findByNameAndUnrepeated(name, unrepeated);
    }
    public Optional<Dictionary> findByVocabularyAndName(Vocabulary vocabulary, String name) {
        return dictionaryRepo.findByVocabularyAndName(vocabulary, name);
    }

    public Optional<Dictionary> findByNameAndParent(String name, Long parent) {
        return dictionaryRepo.findByNameAndParent(name, parent);
    }


    public Page<Dictionary> getDictionaries(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return dictionaryRepo.findAll(pageable);
    }

    public List<Dictionary> saveAll(List<Dictionary> dictionaries) {
        return dictionaryRepo.saveAll(dictionaries);
    }

    public List<Dictionary> findAll() {
        return dictionaryRepo.findAll();
    }

    public List<Dictionary> findDistinctByNameAndParent(List<Dictionary> dictionaries) {
        return dictionaryRepo.findDistinctByNameAndParent(dictionaries);
    }

    public void deleteByIdIn(List<UUID> ids) {
        dictionaryRepo.deleteByIdIn(ids);
    }

    public void deleteByAuthorAndUnrepeated(User author, boolean unrepeated) {
        dictionaryRepo.deleteByAuthorAndUnrepeated(author, unrepeated);
    }
    public void deleteByAuthorAndVocabulary(User author, Vocabulary vocabulary) {
        dictionaryRepo.deleteByAuthorAndVocabulary(author, vocabulary);
    }

    public List<Dictionary> findAllById(List<UUID> ids) {
        return dictionaryRepo.findAllById(ids);
    }

    public Optional<Dictionary> findById(UUID id) {
        return dictionaryRepo.findById(id);
    }

    public Dictionary findByAuthorAndVocabularyAndUnrepeatedAndName(User author, Vocabulary vocabulary, boolean unrepeated, String name) {
        return dictionaryRepo.findByAuthorAndVocabularyAndUnrepeatedAndName(author, vocabulary, unrepeated, name);
    }

    public Dictionary findIfAbsentSaveNewUnrepeated(User author, Vocabulary vocabulary) {
        Dictionary d = findByAuthorAndVocabularyAndUnrepeatedAndName(author, vocabulary, true, "new");
        if (d == null) {
            d = saveNewUnrepeated(author, vocabulary);
        }
        return d;
    }

    public List<Dictionary> findAllByAuthorAndVocabulary(User author, Vocabulary vocabulary) {
        return dictionaryRepo.findAllByAuthorAndVocabulary(author, vocabulary);
    }

    public Dictionary saveNewUnrepeated(User author, Vocabulary vocabulary) {
        Dictionary newDictionary = Dictionary.builder()
                .author(author)
                .vocabulary(vocabulary)
                .unrepeated(true)
                .name("new")
                .build();
        return save(newDictionary);
    }
}
