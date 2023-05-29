package by.sviryd.engvoc.service.card;

import by.sviryd.engvoc.config.DictionaryConfig;
import by.sviryd.engvoc.type.LangLocale;
import by.sviryd.engvoc.util.Pair;
import by.sviryd.engvoc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryBindService {
    @Autowired
    private DictionaryConfig config;

    public boolean isSupportedAbbr(String abbr) {
        if (abbr == null) return false;
        if (abbr.length() < 10) return false;
        String first = abbr.substring(0, 5);
        String second = abbr.substring(5);
        return config.isPresent(LangLocale.getLangLocale(first)) && config.isPresent(LangLocale.getLangLocale(second));
    }

    public String getSourceAbbr(String abbr) {
        if (!isSupportedAbbr(abbr)) return null;
        Pair<String, String> pair = getSourceDestinationAbbr(abbr);
        return pair.getFirst();
    }

    public String getDestinationAbbr(String abbr) {
        if (!isSupportedAbbr(abbr)) return null;
        Pair<String, String> pair = getSourceDestinationAbbr(abbr);
        return pair.getSecond();
    }

    public Integer getSourceAbbrId(String abbr) {
        String sourceAbbr = getSourceAbbr(abbr);
        if (sourceAbbr == null) return null;
        LangLocale l = LangLocale.getLangLocale(sourceAbbr);
        if (l == null) return null;
        return l.getId();
    }

    public Integer getDestinationAbbrId(String abbr) {
        String destinAbbr = getDestinationAbbr(abbr);
        if (destinAbbr == null) return null;
        LangLocale l = LangLocale.getLangLocale(destinAbbr);
        if (l == null) return null;
        return l.getId();
    }

    public boolean isDictionary(String filename) {
        return getDictionaryNameWithoutAbbr(filename) != null;
    }

    public String getDictionaryNameWithoutAbbr(String filename) {
        filename = StringUtil.getFilenameWithoutExtension(filename);
        if (filename == null) return null;
        if (filename.length() < 10) return null;
        int i = filename.length() - 10;
        if (isSupportedAbbr(filename.substring(i))) {
            return filename.substring(0, i);
        }
        return null;
    }

    public String getDictionaryAbbr(String filename) {
        filename = StringUtil.getFilenameWithoutExtension(filename);
        if (filename == null) return null;
        if (filename.length() < 10) return null;
        int i = filename.length() - 10;
        if (isSupportedAbbr(filename.substring(i))) {
            return filename.substring(i);
        }
        return null;
    }

    private Pair<String, String> getSourceDestinationAbbr(String abbr) {
        return new Pair<>(abbr.substring(0, 5), abbr.substring(5));
    }
}
