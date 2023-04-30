package by.sviryd.engvoc.service.card;

import by.sviryd.engvoc.config.DictionaryConfig;
import by.sviryd.engvoc.type.Lang;
import by.sviryd.engvoc.util.Pair;
import by.sviryd.engvoc.util.StringConverterUtil;
import by.sviryd.engvoc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryBindService {
    @Autowired
    private DictionaryConfig config;

    public boolean isSupportedAbbr(String abbr) {
        String first = abbr.substring(0, 2).toLowerCase();
        String second = abbr.substring(2).toLowerCase();
        return config.isPresent(Lang.getLang(first)) && config.isPresent(Lang.getLang(second));
    }

    public String getSourceAbbr(String abbr) {
        if (!isSupportedAbbr(abbr)) return null;
        Pair<String, String> pair = getSourceDestinationAbbr(abbr);
        if (pair == null) return null;
        return pair.getFirst();
    }

    public String getDestinationAbbr(String abbr) {
        if (!isSupportedAbbr(abbr)) return null;
        Pair<String, String> pair = getSourceDestinationAbbr(abbr);
        if (pair == null) return null;
        return pair.getSecond();
    }

    public Integer getSourceAbbrId(String abbr) {
        String sourceAbbr = getSourceAbbr(abbr);
        if (sourceAbbr == null) return null;
        Lang l = Lang.getLang(sourceAbbr);
        if (l == null) return null;
        return l.getId();
    }

    public Integer getDestinationAbbrId(String abbr) {
        String destinAbbr = getDestinationAbbr(abbr);
        if (destinAbbr == null) return null;
        Lang l = Lang.getLang(destinAbbr);
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
        if (isSupportedAbbr(filename.substring(i))) {
            return filename.substring(0, i);
        }
        return null;
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
        String[] parts = StringConverterUtil.splitCamelCaseString(abbr);
        if (parts.length < 2) return null;
        return new Pair<>(parts[0], parts[1]);
    }
}
