package by.sviryd.engvoc.service.card.reader;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Meaning;
import by.sviryd.engvoc.service.card.AbbyyLatinCyrillicSplitterService;
import by.sviryd.engvoc.service.card.DictionaryBindService;
import by.sviryd.engvoc.util.FileExtensionUtil;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class XmlCardReaderService {
    private static final String EMPTY = "";
    private static final String CARD = "card";
    private static final String WORD = "word";
    private static final String MEANINGS = "meanings";
    private static final String MEANING = "meaning";
    private static final String STATISTICS = "statistics";
    private static final String STATUS = "status";
    private static final String SOUND = "sound";
    private static final String NAME = "name";
    private static final int LEARNED_MARK = 4;
    private static final String TRANSLATIONS = "translations";
    private static final String TRANSCRIPTION = "transcription";
    private static final String EXAMPLES = "examples";
    private static final String EXAMPLE = "example";

    @Autowired
    private DictionaryBindService dictionaryConfig;

    @Autowired
    private AbbyyLatinCyrillicSplitterService splitter;

    public List<Card> extract(File file) throws Exception {
        validateIO(file);
        String dictionaryName = dictionaryConfig.getDictionaryNameWithoutAbbr(file.getName());
        if (dictionaryName == null) {
            throw new IllegalArgumentException("File " + file + "is not supported dictionary.");
        }
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return getCards(document, dictionaryName);
    }

    public List<Card> extract(MultipartFile file){
        if (!FileExtensionUtil.isXml(file.getName())) {
            throw new IllegalArgumentException("File " + file + "is not xml.");
        }
        String dictionaryName = dictionaryConfig.getDictionaryNameWithoutAbbr(file.getName());
        if (dictionaryName == null) {
            throw new IllegalArgumentException("File " + file + "is not supported dictionary.");
        }
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(file.getInputStream());
        } catch (Exception e) {
            throw new IllegalArgumentException("Something wrong with " + file.getOriginalFilename());
        }
        return getCards(document, dictionaryName);
    }

    private List<Card> getCards(Document document, String dictionaryName) {
        Dictionary dictionary = new Dictionary(dictionaryName);
        List<Card> cards = new ArrayList<>();
        Element root = document.getRootElement();
        for (Iterator<Element> it = root.elementIterator(CARD); it.hasNext(); ) {
            Element card = it.next();
            String word = getWord(card);
            if (EMPTY.equals(word)) {
                continue;
            }
            List<Element> eMeanings = card
                    .element(MEANINGS)
                    .elements(MEANING);
            List<Meaning> meanings = getMeanings(eMeanings);
            if (!meanings.isEmpty()) {
                for (int i = 0; i < meanings.size(); i++) {
                    Meaning m = meanings.get(i);
                    Element eMeaning = eMeanings.get(i);
                    String transcription = getTranscription(eMeaning);
                    boolean learned = isLearned(eMeaning);
                    String sound = getSound(eMeaning);
                    String translation = m.getCombineWords();
                    String exampleFromXml = m.getExample();
                    Pair<String, String> pair = splitter.getPair(exampleFromXml);
                    String example = pair.getKey();
                    String exampleTranslation = pair.getValue();
                    cards.add(
                            Card
                                    .builder()
                                    .word(word)
                                    .translation(translation)
                                    .example(example)
                                    .exampleTranslation(exampleTranslation)
                                    .transcription(transcription)
                                    .learned(learned)
                                    .sound(sound)
                                    .dictionary(dictionary)
                                    .build()
                    );
                }
            }
        }
        return cards;
    }


    private void validateIO(File file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("File " + file + "is not a file, it is a directory.");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("File " + file + "does not exist.");
        }
        if (!FileExtensionUtil.isXml(file.getName())) {
            throw new IllegalArgumentException("File " + file + "is not xml.");
        }
    }

    private String getSound(Element eMeaning) {
        String sound = null;
        try {
            Element eSound = eMeaning.element(SOUND);
            sound = eSound.attributeValue(NAME);
        } catch (Exception e) {
        }
        return sound;
    }

    private boolean isLearned(Element eMeaning) {
        boolean learned = false;
        try {
            Element eStatistics = eMeaning.element(STATISTICS);
            int indicator = Integer.parseInt(eStatistics.attributeValue(STATUS));
            learned = indicator == LEARNED_MARK;
        } catch (Exception e) {
        }
        return learned;
    }

    private String getTranscription(Element meaning) {
        String transcription = null;
        try {
            transcription = meaning.attributeValue(TRANSCRIPTION);

        } catch (Exception e) {
        }
        return transcription;
    }

    private List<Meaning> getMeanings(List<Element> elements) {
        List<Meaning> meanings = Collections.emptyList();
        try {
            meanings = new ArrayList<>();
            for (Element e : elements) {
                List<Element> elementWords = e.element(TRANSLATIONS).elements(WORD);
                List<String> words = new ArrayList<>();
                if (!elementWords.isEmpty()) {
                    for (Element element : elementWords) {
                        String stringValue = element.getStringValue();
                        if (stringValue == null) continue;
                        words.add(stringValue);
                    }
                }
                String example = null;
                Element elementExample = null;
                try {
                    elementExample = e
                            .element(EXAMPLES)
                            .element(EXAMPLE);
                } catch (Exception e1) {
                }
                if (elementExample != null) {
                    example = elementExample.getStringValue();
                }
                Meaning meaning = new Meaning();
                meaning.setCombineWords(words);
                meaning.setExample(example);
                meanings.add(meaning);
            }
        } catch (Exception e) {
        }
        return meanings;
    }

    private String getWord(Element card) {
        try {
            return card.element(WORD).getStringValue();
        } catch (Exception e) {
            return EMPTY;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    static class Meaning {
        private static final String SPLITTER = "; ";
        private static final String EMPTY = "";
        private String combineWords = EMPTY;
        private String example;

        public void setCombineWords(List<String> words) {
            if (words == null || words.isEmpty()) return;
            if (EMPTY.equals(combineWords)) {
                combineWords = words.get(0);
                for (int i = 1; i < words.size(); i++) {
                    combineWords = combineWords + SPLITTER + words.get(i);
                }
            } else {
                for (String word : words) {
                    combineWords = combineWords + SPLITTER + word;
                }
            }
        }
    }
}
