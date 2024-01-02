package by.sviryd.engvoc.service.entityAdjuster;

import by.sviryd.engvoc.domain.Card;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UploadCardEntityAdjusterService {
    public List<Card> adjust(List<Card> cards) {
        cards.parallelStream().forEach(c -> {
            String word;
            if ((word = c.getWord()) != null && word.length() > 255) {
                c.setWord(word.substring(0, 255));
            }
            String translation;
            if ((translation = c.getTranslation()) != null && translation.length() > 255) {
                c.setTranslation(translation.substring(0, 255));
            }
            String example;
            if ((example = c.getExample()) != null && example.length() > 510) {
                c.setExample(example.substring(0, 510));
            }
            String exampleTranslation;
            if ((exampleTranslation = c.getExampleTranslation()) != null && exampleTranslation.length() > 510) {
                c.setExampleTranslation(exampleTranslation.substring(0, 510));
            }
            String transcription;
            if ((transcription = c.getTranscription()) != null && transcription.length() > 255) {
                c.setTranscription(transcription.substring(0, 255));
            }
        });
        return cards;
    }
}
