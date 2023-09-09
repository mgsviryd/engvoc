package by.sviryd.engvoc.repos;

import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.type.LangLocale;

import java.util.List;

public interface DictionaryCustomRepo {
    List<Dictionary> findDistinctByNameAndParent(List<Dictionary> dictionaries);
}
