package by.sviryd.engvoc.service.card;

import by.sviryd.engvoc.config.DictionaryConfig;
import by.sviryd.engvoc.util.Pair;
import by.sviryd.engvoc.util.StringConverterUtil;
import by.sviryd.engvoc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryBindService {
    @Autowired
    private DictionaryConfig config;

    public String getDbName(){return config.getDbName();}
    public String getUploadName(){return config.getUploadName();}

    public boolean isSupportedAbbr(String abbr) {
        return config.getSupported().contains(abbr);
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
        return config.getIds().get((config.getAbbr().indexOf(sourceAbbr)));
    }

    public Integer getDestinationAbbrId(String abbr) {
        String destinAbbr = getDestinationAbbr(abbr);
        if (destinAbbr == null) return null;
        return config.getIds().get(config.getAbbr().indexOf(destinAbbr));
    }

    public boolean isDictionary(String filename) {
        return getDictionaryNameWithoutAbbr(filename) != null;
    }

    public String getDictionaryNameWithoutAbbr(String filename) {
        filename = StringUtil.getFilenameWithoutExtension(filename);
        if (filename == null) return null;
        for (String s : config.getSupported()) {
            if (filename.endsWith(s)) {
                int last = filename.lastIndexOf(s);
                return filename.substring(0, last);
            }
        }
        return null;
    }

    public String getDictionaryAbbr(String filename) {
        filename = StringUtil.getFilenameWithoutExtension(filename);
        if(filename == null) return null;
        for (String s : config.getSupported()) {
            if (filename.endsWith(s)) {
                int last = filename.lastIndexOf(s);
                return filename.substring(last);
            }
        }
        return null;
    }

    private Pair<String, String> getSourceDestinationAbbr(String abbr) {
        String[] parts = StringConverterUtil.splitCamelCaseString(abbr);
        if (parts.length < 2) return null;
        return new Pair<>(parts[0], parts[1]);
    }
}
