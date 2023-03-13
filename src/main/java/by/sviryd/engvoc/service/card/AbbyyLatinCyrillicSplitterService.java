package by.sviryd.engvoc.service.card;

import by.sviryd.engvoc.config.AbbyyLatinCyrillicSplitterConfig;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbbyyLatinCyrillicSplitterService {
    @Autowired
    private AbbyyLatinCyrillicSplitterConfig config;

    public Pair<String, String> getPair(String string) {
        if (string == null) return new Pair<>(null, null);
        String first = null;
        String last = null;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (isCyrillic(c)) {
                first = string.substring(0, i).trim();
                first = removeSuffixesAndTrim(
                        first,
                        config.getSuffix_1(),
                        config.getSuffix_2(),
                        config.getSuffix_3()
                );
                last = string.substring(i).trim();
                break;
            }
        }
        return new Pair<>(first, last);
    }

    private String removeSuffixesAndTrim(String first, String... splitters) {
        for (String splitter : splitters) {
            if (first != null && first.endsWith(splitter)) {
                first = first.substring(0, first.length() - splitter.length()).trim();
            }
        }
        return first;
    }

    boolean isCyrillic(char c) {
        return Character.UnicodeBlock.CYRILLIC.equals(Character.UnicodeBlock.of(c));
    }
}
