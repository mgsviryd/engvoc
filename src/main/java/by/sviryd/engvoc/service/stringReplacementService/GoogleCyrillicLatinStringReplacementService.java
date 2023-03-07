package by.sviryd.engvoc.service.stringReplacementService;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleCyrillicLatinStringReplacementService implements IStringReplacementService {
    private static final Map<String, String> PAIRS = new HashMap<>();

    static {
        PAIRS.put("А", "A");
        PAIRS.put("Б", "B");
        PAIRS.put("В", "V");
        PAIRS.put("Г", "G");
        PAIRS.put("Д", "D");
        PAIRS.put("Е", "E");
        PAIRS.put("Ё", "Yo");
        PAIRS.put("Ж", "Zh");
        PAIRS.put("З", "Z");
        PAIRS.put("И", "I");
        PAIRS.put("Й", "Y");
        PAIRS.put("К", "K");
        PAIRS.put("Л", "L");
        PAIRS.put("М", "M");
        PAIRS.put("Н", "N");
        PAIRS.put("О", "O");
        PAIRS.put("П", "P");
        PAIRS.put("Р", "R");
        PAIRS.put("С", "S");
        PAIRS.put("Т", "T");
        PAIRS.put("У", "U");
        PAIRS.put("Ф", "F");
        PAIRS.put("Х", "Kh");
        PAIRS.put("Ц", "Ts");
        PAIRS.put("Ч", "Ch");
        PAIRS.put("Ш", "Sh");
        PAIRS.put("Щ", "Shch");
        PAIRS.put("Ъ", "\"");
        PAIRS.put("Ы", "Y");
        PAIRS.put("Ь", "'");
        PAIRS.put("Э", "E");
        PAIRS.put("Ю", "Yu");
        PAIRS.put("Я", "ya");
        PAIRS.put("а", "a");
        PAIRS.put("б", "b");
        PAIRS.put("в", "v");
        PAIRS.put("г", "g");
        PAIRS.put("д", "d");
        PAIRS.put("е", "e");
        PAIRS.put("ё", "yo");
        PAIRS.put("ж", "zh");
        PAIRS.put("з", "z");
        PAIRS.put("и", "i");
        PAIRS.put("й", "y");
        PAIRS.put("к", "k");
        PAIRS.put("л", "l");
        PAIRS.put("м", "m");
        PAIRS.put("н", "n");
        PAIRS.put("о", "o");
        PAIRS.put("п", "p");
        PAIRS.put("р", "r");
        PAIRS.put("с", "s");
        PAIRS.put("т", "t");
        PAIRS.put("у", "u");
        PAIRS.put("ф", "f");
        PAIRS.put("х", "kh");
        PAIRS.put("ц", "ts");
        PAIRS.put("ч", "ch");
        PAIRS.put("ш", "sh");
        PAIRS.put("щ", "shch");
        PAIRS.put("ъ", "\"");
        PAIRS.put("ы", "y");
        PAIRS.put("ь", "'");
        PAIRS.put("э", "e");
        PAIRS.put("ю", "yu");
        PAIRS.put("я", "ya");
    }

    @Override
    public String replace(String string) {
        return IStringReplacementService.replace(PAIRS, string);
    }
}
