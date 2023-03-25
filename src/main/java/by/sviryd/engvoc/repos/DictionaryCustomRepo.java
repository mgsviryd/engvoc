package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Dictionary;

import java.util.List;

public interface DictionaryCustomRepo {
    List<Dictionary> findDistinctByNameAndParent(List<Dictionary> dictionaries);
}
