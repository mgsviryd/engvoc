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

    //TODO - SQL Error: 1062, SQLState: 23000 in multithread
    public Dictionary saveShaneOfDictionary(List<Dictionary> dictionaries) {
        Dictionary parent = null;
        Long parentId = 0L;
        for (Dictionary c : dictionaries) {
            if (parent != null) {
                parentId = parent.getId();
            }
            Optional<Dictionary> optional = findByNameAndParent(c.getName(), parentId);
            if (optional.isPresent()) {
                parent = optional.get();
            } else {
                c.setParent(parentId);
                parent = save(c);
            }
        }
        return parent;
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

    public void deleteByIdIn(List<Long> ids) {
        dictionaryRepo.deleteByIdIn(ids);
    }

    public void deleteByUnique(boolean unique) {
        dictionaryRepo.deleteByUnique(unique);
    }

    public List<Dictionary> findAllById(List<Long> ids) {
        return dictionaryRepo.findAllById(ids);
    }

    public Optional<Dictionary> findById(Long id) {
        return dictionaryRepo.findById(id);
    }
}
