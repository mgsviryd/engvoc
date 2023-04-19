package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardUploadService {
    @Autowired
    private CardService cardService;
    @Autowired
    private DictionaryService dictionaryService;

    public HashMap<Object, Object> saveNewDictionariesAndCardsBatch(List<Card> cards){
        if (cards.isEmpty()) return getEmpty();
        Set<Dictionary> dictionaries = cards.stream().map(Card::getDictionary).filter(Objects::nonNull).collect(Collectors.toSet());
        if (dictionaries.isEmpty()) return getEmpty();
        LocalDateTime now = LocalDateTime.now();
        dictionaries.forEach(d -> d.setCreationLDT(now));
        cards.forEach(c -> c.setCreationLDT(now));
        List<Dictionary> dsSaved = dictionaryService.saveAll(new ArrayList<>(dictionaries));
        cards.forEach(c -> c.setDictionary(dsSaved.stream().filter(x-> c.getDictionary().getName().equals(x.getName())).findFirst().get()));
        List<Card> csSaved = cardService.saveAll(cards);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("cards", csSaved);
        data.put("dictionaries", dsSaved);
        return data;
    }
    private HashMap<Object, Object> getEmpty(){
        HashMap<Object, Object> data = new HashMap<>();
        data.put("cards", Collections.emptyList());
        data.put("dictionaries", Collections.emptyList());
        return data;
    }
}
