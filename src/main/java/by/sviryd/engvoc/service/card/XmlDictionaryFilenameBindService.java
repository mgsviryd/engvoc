package by.sviryd.engvoc.service.card;

import by.sviryd.engvoc.config.DictionaryConfig;
import by.sviryd.engvoc.type.LangLocale;
import by.sviryd.engvoc.util.Pair;
import by.sviryd.engvoc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XmlDictionaryFilenameBindService {
    @Autowired
    private DictionaryConfig config;

    public boolean isSupportedAbbr(String abbr) {
        if (abbr == null) return false;
        if (abbr.length() < 4) return false;
        String first = abbr.substring(0, 2);
        String second = abbr.substring(2);
        return config.isPresent(LangLocale.getByLangAndLocale(first, second));
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

    public String getSourceAbbrId(String abbr) {
        String sourceAbbr = getSourceAbbr(abbr);
        if (sourceAbbr == null) return null;
        LangLocale l = LangLocale.findLangLocale(sourceAbbr);
        if (l == null) return null;
        return l.getId();
    }

    public String getDestinationAbbrId(String abbr) {
        String destinAbbr = getDestinationAbbr(abbr);
        if (destinAbbr == null) return null;
        LangLocale l = LangLocale.findLangLocale(destinAbbr);
        if (l == null) return null;
        return l.getId();
    }

    public boolean isDictionary(String filename) {
        return getDictionaryNameWithoutAbbr(filename) != null;
    }

    public String getDictionaryNameWithoutAbbr(String filename) {
        filename = StringUtil.getFilenameWithoutExtension(filename);
        if (filename == null) return null;
        if (filename.length() < 4) return null;
        int i = filename.length() - 4;
        return filename.substring(0, i);
    }

    public String getDictionaryAbbr(String filename) {
        filename = StringUtil.getFilenameWithoutExtension(filename);
        if (filename == null) return null;
        if (filename.length() < 4) return null;
        int i = filename.length() - 4;
        if (isSupportedAbbr(filename.substring(i))) {
            return filename.substring(i);
        }
        return null;
    }

    private Pair<String, String> getSourceDestinationAbbr(String abbr) {
        return new Pair<>(abbr.substring(0, 2), abbr.substring(2));
    }
}
