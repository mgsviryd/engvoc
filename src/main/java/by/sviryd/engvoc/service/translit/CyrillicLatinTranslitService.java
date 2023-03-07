package by.sviryd.engvoc.service.translit;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
/**
 * Standard Google
 */
public class CyrillicLatinTranslitService implements ITranslitService {
    private static final Map<String, String> letters = new HashMap<>();
    static {
        letters.put("А", "A");
        letters.put("Б", "B");
        letters.put("В", "V");
        letters.put("Г", "G");
        letters.put("Д", "D");
        letters.put("Е", "E");
        letters.put("Ё", "Yo");
        letters.put("Ж", "Zh");
        letters.put("З", "Z");
        letters.put("И", "I");
        letters.put("Й", "Y");
        letters.put("К", "K");
        letters.put("Л", "L");
        letters.put("М", "M");
        letters.put("Н", "N");
        letters.put("О", "O");
        letters.put("П", "P");
        letters.put("Р", "R");
        letters.put("С", "S");
        letters.put("Т", "T");
        letters.put("У", "U");
        letters.put("Ф", "F");
        letters.put("Х", "Kh");
        letters.put("Ц", "Ts");
        letters.put("Ч", "Ch");
        letters.put("Ш", "Sh");
        letters.put("Щ", "Shch");
        letters.put("Ъ", "\"");
        letters.put("Ы", "Y");
        letters.put("Ь", "'");
        letters.put("Э", "E");
        letters.put("Ю", "Yu");
        letters.put("Я", "ya");
        letters.put("а", "a");
        letters.put("б", "b");
        letters.put("в", "v");
        letters.put("г", "g");
        letters.put("д", "d");
        letters.put("е", "e");
        letters.put("ё", "yo");
        letters.put("ж", "zh");
        letters.put("з", "z");
        letters.put("и", "i");
        letters.put("й", "y");
        letters.put("к", "k");
        letters.put("л", "l");
        letters.put("м", "m");
        letters.put("н", "n");
        letters.put("о", "o");
        letters.put("п", "p");
        letters.put("р", "r");
        letters.put("с", "s");
        letters.put("т", "t");
        letters.put("у", "u");
        letters.put("ф", "f");
        letters.put("х", "kh");
        letters.put("ц", "ts");
        letters.put("ч", "ch");
        letters.put("ш", "sh");
        letters.put("щ", "shch");
        letters.put("ъ", "\"");
        letters.put("ы", "y");
        letters.put("ь", "'");
        letters.put("э", "e");
        letters.put("ю", "yu");
        letters.put("я", "ya");
    }
    @Override
    public String toTranslit(String text) {
        if (text!=null){
            return toTranslit(letters, text);
        }else {
            return null;
        }
    }
    public List<String> toTranslit(List<String> texts){
        return texts.stream().map(this::toTranslit).collect(Collectors.toList());
    }
}
