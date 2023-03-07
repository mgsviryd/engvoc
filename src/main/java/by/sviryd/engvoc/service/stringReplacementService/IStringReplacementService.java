package by.sviryd.engvoc.service.stringReplacementService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IStringReplacementService {
    String replace(String text);
    static String replace(Map<String, String> letters, String text){
        if (letters == null) throw new IllegalArgumentException("Argument letters must not be null.");
        if (text == null) throw new IllegalArgumentException("Argument text must not be null.");
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i<text.length(); i++) {
            String l = String.valueOf(text.charAt(i));
            if (letters.containsKey(l)) {
                sb.append(letters.get(l));
            }
            else {
                sb.append(l);
            }
        }
        return sb.toString();
    }
    static String replace(String word, Map<String, String> words){
        if (words == null) throw new IllegalArgumentException("Argument letters must not be null.");
        if (word == null) throw new IllegalArgumentException("Argument text must not be null.");
        String result = words.get(word);
        if (result != null){
            return result;
        }else {
            return word;
        }
    }
    static String replace(String text, IStringReplacementService... services){
        if (text == null) throw new IllegalArgumentException("Argument text must not be null.");
        for (IStringReplacementService service: services             ) {
            text = service.replace(text);
        }
        return text;
    }
    default List<String> replace(List<String> texts){
        return texts.stream().map(this::replace).collect(Collectors.toList());
    }
}
