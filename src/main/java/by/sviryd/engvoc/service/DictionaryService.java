package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Dictionary;
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

    public Optional<Dictionary> findByNameAndUnique(String name, Boolean unique) {
        return dictionaryRepo.findByNameAndUnique(name, unique);
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

    public void deleteByUnique(boolean unique) {
        dictionaryRepo.deleteByUnique(unique);
    }

    public List<Dictionary> findAllById(List<UUID> ids) {
        return dictionaryRepo.findAllById(ids);
    }

    public Optional<Dictionary> findById(UUID id) {
        return dictionaryRepo.findById(id);
    }
}
