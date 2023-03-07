package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.URLConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class URLReservedWordsCommonService {
    @Autowired
    private URLConfig urlConfig;

    public boolean isReserved(String word){
        return urlConfig.getReservedWords().contains(word);
    }
}
